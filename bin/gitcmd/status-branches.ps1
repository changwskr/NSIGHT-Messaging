#Requires -Version 5.1
$OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::UTF8
$ErrorActionPreference = 'Stop'

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = (Resolve-Path (Join-Path $ScriptDir '..\..')).Path
$GitRemote = if ($env:GIT_REMOTE) { $env:GIT_REMOTE } else { 'origin' }

Set-Location $ProjectRoot

Write-Host "==> Project: $ProjectRoot"
Write-Host "==> 현재 브랜치: $(git branch --show-current)"
Write-Host ''
Write-Host "==> git fetch $GitRemote"
git fetch $GitRemote
Write-Host ''
Write-Host '==> branch -vv'
git branch -vv
Write-Host ''

foreach ($br in @('develop', 'master')) {
    git rev-parse --verify $br 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) {
        $c = (git rev-list --left-right --count "${br}...${GitRemote}/${br}") -split '\s+'
        Write-Host "--- $br vs ${GitRemote}/${br} ---"
        Write-Host "    앞섬 (ahead) $($c[0]) / 뒤처짐 (behind) $($c[1])"
        Write-Host ''
    }
}

Write-Host 'master 비교: bin\gitcmd\compare-remote.bat'
Write-Host 'develop pull: $env:GIT_BRANCH=''develop''; bin\gitcmd\pull-remote.bat'
