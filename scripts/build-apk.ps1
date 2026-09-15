<#
Build the Android app and copy the debug APK into the project's dist folder.

Usage (from any directory):
    powershell -ExecutionPolicy Bypass -File scripts\build-apk.ps1

Notes:
  - The toolchain (Android SDK, Gradle, caches) lives in <project folder>\tools
    and is picked up automatically.
  - Gradle and Android caches are redirected into tools, so nothing is written
    to the user profile.
  - Keep this file ASCII-only: Windows PowerShell 5.1 reads BOM-less files using
    the system code page and would fail to parse non-ASCII text.
#>
[CmdletBinding()]
param(
    [string]$ToolsDir = ""
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$workspace = Split-Path -Parent $repoRoot

if ([string]::IsNullOrWhiteSpace($ToolsDir)) {
    $candidate = Join-Path $workspace "tools"
    if (Test-Path $candidate) { $ToolsDir = $candidate }
}

# 1) JDK: prefer JAVA_HOME, otherwise use the newest JDK found on this machine.
if (-not $env:JAVA_HOME) {
    $jdk = Get-ChildItem "C:\Program Files\Microsoft\jdk-*" -Directory -ErrorAction SilentlyContinue |
        Sort-Object Name -Descending | Select-Object -First 1
    if ($jdk) {
        $env:JAVA_HOME = $jdk.FullName
        Write-Host "JDK: $env:JAVA_HOME"
    }
}
if (-not $env:JAVA_HOME) {
    throw "No JDK found. Install JDK 17+ or set JAVA_HOME."
}

# 2) Keep Gradle and Android caches inside the project folder.
if ($ToolsDir) {
    $env:GRADLE_USER_HOME = Join-Path $ToolsDir "gradle-home"
    $env:ANDROID_USER_HOME = Join-Path $ToolsDir "android-user-home"
    New-Item -ItemType Directory -Force -Path $env:GRADLE_USER_HOME, $env:ANDROID_USER_HOME | Out-Null
}

# 3) Clear proxy environment variables; the proxy is configured in
#    tools\gradle-home\gradle.properties instead.
$env:HTTP_PROXY = ""; $env:HTTPS_PROXY = ""; $env:ALL_PROXY = ""
$env:http_proxy = ""; $env:https_proxy = ""; $env:all_proxy = ""

# 4) Prefer the local Gradle 8.9 distribution, fall back to the repo wrapper.
$gradle = $null
if ($ToolsDir) {
    $local = Join-Path $ToolsDir "gradle\gradle-8.9\bin\gradle.bat"
    if (Test-Path $local) { $gradle = $local }
}
if (-not $gradle) { $gradle = Join-Path $repoRoot "gradlew.bat" }

Push-Location $repoRoot
try {
    Write-Host "Gradle: $gradle"
    & $gradle assembleDebug --console=plain
    if ($LASTEXITCODE -ne 0) { throw "Build failed (exit code $LASTEXITCODE)." }

    $apk = Join-Path $repoRoot "app\build\outputs\apk\debug\app-debug.apk"
    $dist = Join-Path $workspace "dist"
    New-Item -ItemType Directory -Force -Path $dist | Out-Null
    $target = Join-Path $dist "CSLineups-debug.apk"
    Copy-Item -LiteralPath $apk -Destination $target -Force

    Write-Host ""
    Write-Host "APK ready: $target"
    Write-Host "Copy it to the phone and install (allow unknown sources once)."
}
finally {
    Pop-Location
}

