<#
Generate launcher icons from a source logo image.

Usage:
    powershell -ExecutionPolicy Bypass -File scripts\make-icons.ps1
    powershell -ExecutionPolicy Bypass -File scripts\make-icons.ps1 -Style light
    powershell -ExecutionPolicy Bypass -File scripts\make-icons.ps1 -Source path\to\logo.png

What it does:
  1. crops the source to the logo's bounding box (drops the empty margin)
  2. keys out the white background
  3. dark style (default): flips the black lettering to white so it reads on a
     dark tile; light style keeps the original black-on-white brand look
  4. writes adaptive-icon foreground layers, plain square/round fallback icons
     for every density, and keeps ic_launcher_background in colors.xml in sync

Keep this file ASCII-only: Windows PowerShell 5.1 reads BOM-less files using
the system code page and would fail to parse non-ASCII text.
#>
[CmdletBinding()]
param(
    [string]$Source = "",
    [ValidateSet("light", "dark")][string]$Style = "dark"
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing

$repoRoot = Split-Path -Parent $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($Source)) {
    $Source = Join-Path $repoRoot "docs\branding\logo-csu.png"
}
$resDir = Join-Path $repoRoot "app\src\main\res"

if (-not (Test-Path -LiteralPath $Source)) { throw "Source image not found: $Source" }

$tileColor = if ($Style -eq "dark") { [System.Drawing.Color]::FromArgb(255, 17, 17, 20) } else { [System.Drawing.Color]::White }
$tileHex = if ($Style -eq "dark") { "#FF111114" } else { "#FFFFFFFF" }

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
Write-Host ("logo area: {0}x{1}, style: {2}" -f $cropped.Width, $cropped.Height, $Style)

# 2) master: white removed, lettering flipped to white for the dark style
$masterWidth = 420
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
            $master.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0))
        }
        elseif ($Style -eq "dark" -and $c.R -lt 90 -and $c.G -lt 90 -and $c.B -lt 90) {
            # near-black lettering -> white, keep the red accent as is
            $master.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(255, 255, 255, 255))
        }
    }
}

# 3) adaptive icon foreground layers (108dp canvas, logo at 64% so no mask clips it)
$densities = [ordered]@{ "mdpi" = 108; "hdpi" = 162; "xhdpi" = 216; "xxhdpi" = 324; "xxxhdpi" = 432 }
foreach ($entry in $densities.GetEnumerator()) {
    $size = [int]$entry.Value
    $canvas = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($canvas)
    $g.Clear([System.Drawing.Color]::Transparent)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $w = [int]($size * 0.64)
    $h = [int]($masterHeight * $w / $masterWidth)
    $g.DrawImage($master, [int](($size - $w) / 2), [int](($size - $h) / 2), $w, $h)
    $g.Dispose()

    $outDir = Join-Path $resDir ("mipmap-" + $entry.Key)
    New-Item -ItemType Directory -Force -Path $outDir | Out-Null
    $canvas.Save((Join-Path $outDir "ic_launcher_foreground.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $canvas.Dispose()
    Write-Host ("adaptive foreground: mipmap-{0}/ic_launcher_foreground.png ({1}x{1})" -f $entry.Key, $size)
}

# 4) plain square/round fallback icons (launchers that ignore adaptive icons)
$legacy = [ordered]@{ "mdpi" = 48; "hdpi" = 72; "xhdpi" = 96; "xxhdpi" = 144; "xxxhdpi" = 192 }
foreach ($entry in $legacy.GetEnumerator()) {
    $size = [int]$entry.Value
    $outDir = Join-Path $resDir ("mipmap-" + $entry.Key)
    New-Item -ItemType Directory -Force -Path $outDir | Out-Null

    $square = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($square)
    $g.Clear($tileColor)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $w = [int]($size * 0.74)
    $h = [int]($masterHeight * $w / $masterWidth)
    $g.DrawImage($master, [int](($size - $w) / 2), [int](($size - $h) / 2), $w, $h)
    $g.Dispose()
    $square.Save((Join-Path $outDir "ic_launcher.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $square.Dispose()

    # round: keep it fully opaque and full-bleed. Launchers apply their own
    # mask (circle/squircle) to legacy icons anyway, and a transparent icon
    # would make some launchers draw an extra background plate.
    $round = New-Object System.Drawing.Bitmap($size, $size, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $g = [System.Drawing.Graphics]::FromImage($round)
    $g.Clear($tileColor)
    $g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $w = [int]($size * 0.70)
    $h = [int]($masterHeight * $w / $masterWidth)
    $g.DrawImage($master, [int](($size - $w) / 2), [int](($size - $h) / 2), $w, $h)
    $g.Dispose()
    $round.Save((Join-Path $outDir "ic_launcher_round.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $round.Dispose()

    Write-Host ("fallback icons: mipmap-{0}/ic_launcher.png + ic_launcher_round.png ({1}x{1})" -f $entry.Key, $size)
}

# 5) keep the adaptive background color in sync with the chosen style
$colorsPath = Join-Path $resDir "values\colors.xml"
$colors = Get-Content -LiteralPath $colorsPath -Raw
$colors = $colors -replace '<color name="ic_launcher_background">[^<]*</color>', ('<color name="ic_launcher_background">' + $tileHex + '</color>')
[System.IO.File]::WriteAllText($colorsPath, $colors, (New-Object System.Text.UTF8Encoding($false)))
Write-Host ("adaptive background set to {0} in values/colors.xml" -f $tileHex)

$master.Dispose(); $cropped.Dispose(); $src.Dispose()
Write-Host "done."
