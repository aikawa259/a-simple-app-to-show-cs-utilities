<#
Generate launcher icons from a source logo image.

Usage:
    powershell -ExecutionPolicy Bypass -File scripts\make-icons.ps1
    powershell -ExecutionPolicy Bypass -File scripts\make-icons.ps1 -Source path\to\logo.png

What it does:
  1. crops the source to the logo's bounding box (drops the empty margin)
  2. removes the white background so the logo can sit on any background color
  3. writes adaptive-icon foreground layers for every density

The adaptive icon itself is res/mipmap-anydpi-v26/ic_launcher.xml
(white background + this foreground), so no legacy raster icons are needed
for minSdk 26+.

Keep this file ASCII-only: Windows PowerShell 5.1 reads BOM-less files using
the system code page and would fail to parse non-ASCII text.
#>
[CmdletBinding()]
param(
    [string]$Source = ""
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing

$repoRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($Source)) {
    $Source = Join-Path $repoRoot "docs\branding\logo-csu.png"
}
$resDir = Join-Path $repoRoot "app\src\main\res"

if (-not (Test-Path -LiteralPath $Source)) { throw "Source image not found: $Source" }

$src = [System.Drawing.Bitmap]::FromFile($Source)

# 1) bounding box of non-white pixels (coarse scan is enough)
$minX = $src.Width; $minY = $src.Height; $maxX = 0; $maxY = 0
for ($y = 0; $y -lt $src.Height; $y += 2) {
    for ($x = 0; $x -lt $src.Width; $x += 2) {
        $c = $src.GetPixel($x, $y)
        if ($c.R -lt 240 -or $c.G -lt 240 -or $c.B -lt 240) {
            if ($x -lt $minX) { $minX = $x }
            if ($x -gt $maxX) { $maxX = $x }
            if ($y -lt $minY) { $minY = $y }
            if ($y -gt $maxY) { $maxY = $y }
        }
    }
}
if ($maxX -le $minX -or $maxY -le $minY) { throw "Could not find the logo inside the image." }

$pad = 4
$minX = [Math]::Max(0, $minX - $pad); $minY = [Math]::Max(0, $minY - $pad)
$maxX = [Math]::Min($src.Width - 1, $maxX + $pad); $maxY = [Math]::Min($src.Height - 1, $maxY + $pad)
$cropRect = New-Object System.Drawing.Rectangle($minX, $minY, ($maxX - $minX + 1), ($maxY - $minY + 1))
$cropped = $src.Clone($cropRect, $src.PixelFormat)
Write-Host ("logo area: {0}x{1}" -f $cropped.Width, $cropped.Height)

# 2) master copy with the white background removed, sized for the densest icon
$masterWidth = 380
$masterHeight = [int]($cropped.Height * $masterWidth / $cropped.Width)
$master = New-Object System.Drawing.Bitmap($masterWidth, $masterHeight, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($master)
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.DrawImage($cropped, 0, 0, $masterWidth, $masterHeight)
$g.Dispose()

for ($y = 0; $y -lt $masterHeight; $y++) {
    for ($x = 0; $x -lt $masterWidth; $x++) {
        $c = $master.GetPixel($x, $y)
        if ($c.R -ge 240 -and $c.G -ge 240 -and $c.B -ge 240) {
            $master.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 255, 255, 255))
        }
    }
}

# 3) adaptive icon foreground layers: logo at 64% of the 108dp canvas
$densities = [ordered]@{ "mdpi" = 108; "hdpi" = 162; "xhdpi" = 216; "xxhdpi" = 324; "xxxhdpi" = 432 }
foreach ($entry in $densities.GetEnumerator()) {
    $size = [int]$entry.Value
    $canvas = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($canvas)
    $g.Clear([System.Drawing.Color]::Transparent)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $targetWidth = [int]($size * 0.64)
    $targetHeight = [int]($masterHeight * $targetWidth / $masterWidth)
    $x = [int](($size - $targetWidth) / 2)
    $y = [int](($size - $targetHeight) / 2)
    $g.DrawImage($master, $x, $y, $targetWidth, $targetHeight)
    $g.Dispose()

    $outDir = Join-Path $resDir ("mipmap-" + $entry.Key)
    New-Item -ItemType Directory -Force -Path $outDir | Out-Null
    $outPath = Join-Path $outDir "ic_launcher_foreground.png"
    $canvas.Save($outPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $canvas.Dispose()
    Write-Host ("wrote {0} ({1}x{1})" -f $outPath.Replace($repoRoot, ""), $size)
}

# 4) plain PNG icons as a fallback: many third-party launchers ignore adaptive
#    icons and would otherwise show the system default icon.
$legacy = [ordered]@{ "mdpi" = 48; "hdpi" = 72; "xhdpi" = 96; "xxhdpi" = 144; "xxxhdpi" = 192 }
foreach ($entry in $legacy.GetEnumerator()) {
    $size = [int]$entry.Value
    $outDir = Join-Path $resDir ("mipmap-" + $entry.Key)
    New-Item -ItemType Directory -Force -Path $outDir | Out-Null

    # square: white background, logo at 72% of the icon width
    $square = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($square)
    $g.Clear([System.Drawing.Color]::White)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $w = [int]($size * 0.72)
    $h = [int]($masterHeight * $w / $masterWidth)
    $g.DrawImage($master, [int](($size - $w) / 2), [int](($size - $h) / 2), $w, $h)
    $g.Dispose()
    $square.Save((Join-Path $outDir "ic_launcher.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $square.Dispose()

    # round: white circle, logo kept inside the circle
    $round = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($round)
    $g.Clear([System.Drawing.Color]::Transparent)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
    $g.FillEllipse([System.Drawing.Brushes]::White, 0, 0, $size - 1, $size - 1)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $w = [int]($size * 0.62)
    $h = [int]($masterHeight * $w / $masterWidth)
    $g.DrawImage($master, [int](($size - $w) / 2), [int](($size - $h) / 2), $w, $h)
    $g.Dispose()
    $round.Save((Join-Path $outDir "ic_launcher_round.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $round.Dispose()

    Write-Host ("wrote mipmap-{0}\ic_launcher.png + ic_launcher_round.png ({1}x{1})" -f $entry.Key, $size)
}

$master.Dispose(); $cropped.Dispose(); $src.Dispose()
Write-Host "done."
