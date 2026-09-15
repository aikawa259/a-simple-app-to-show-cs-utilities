<#
Push this repository to GitHub.

Usage:
  1) Create an empty repository on the GitHub website first (recommended), then:
       powershell -ExecutionPolicy Bypass -File scripts\upload-to-github.ps1 `
           -RepoUrl https://github.com/<your-name>/cs-lineups.git

  2) If GitHub CLI (gh) is installed and signed in, let the script create the
     repository as well:
       powershell -ExecutionPolicy Bypass -File scripts\upload-to-github.ps1 -Create

The first push opens a browser window to sign in to GitHub. Credentials are kept
by Windows Credential Manager, never in this repository.
Add -Proxy http://127.0.0.1:7897 if this machine needs the local proxy.

Keep this file ASCII-only: Windows PowerShell 5.1 reads BOM-less files using the
system code page and would fail to parse non-ASCII text.
#>
[CmdletBinding()]
param(
    [string]$RepoUrl = "",
    [switch]$Create,
    [string]$RepoName = "cs-lineups",
    [ValidateSet("public", "private")][string]$Visibility = "public",
    [string]$Proxy = ""
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
Push-Location $repoRoot

try {
    $pending = git status --porcelain
    if ($pending) {
        Write-Host "Uncommitted changes exist; commit them before uploading:" -ForegroundColor Yellow
        Write-Host $pending
    }

    $gh = Get-Command gh -ErrorAction SilentlyContinue
    if ($Create -and $gh) {
        Write-Host "Creating repository with GitHub CLI and pushing..."
        $flags = @("repo", "create", $RepoName, "--source", ".", "--push", "--$Visibility")
        & gh @flags
        if ($LASTEXITCODE -ne 0) { throw "gh repo create failed." }
        Write-Host "Done." -ForegroundColor Green
        return
    }

    if (-not $RepoUrl) {
        Write-Host "No repository URL given." -ForegroundColor Yellow
        Write-Host "Create an empty repository on GitHub (no README, no .gitignore), then run:"
        Write-Host "    powershell -ExecutionPolicy Bypass -File scripts\upload-to-github.ps1 -RepoUrl https://github.com/<your-name>/cs-lineups.git"
        return
    }

    $gitArgs = @()
    if ($Proxy) { $gitArgs += @("-c", "http.proxy=$Proxy", "-c", "https.proxy=$Proxy") }

    $remotes = git remote
    if ($remotes -contains "origin") {
        & git @gitArgs remote set-url origin $RepoUrl
    }
    else {
        & git @gitArgs remote add origin $RepoUrl
    }

    & git @gitArgs push -u origin main
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Push failed. Usually this means you are not signed in to GitHub yet." -ForegroundColor Yellow
        Write-Host "Run 'gh auth login', or finish the browser sign-in prompt and retry."
        return
    }

    Write-Host "Pushed: $RepoUrl" -ForegroundColor Green
}
finally {
    Pop-Location
}
