#Requires -Version 5.1
$OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::UTF8
$ErrorActionPreference = 'Stop'

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = (Resolve-Path (Join-Path $ScriptDir '..\..')).Path
$GitRemote = if ($env:GIT_REMOTE) { $env:GIT_REMOTE } else { 'origin' }
$GitBranch = if ($env:GIT_BRANCH) { $env:GIT_BRANCH } else { 'master' }

Set-Location $ProjectRoot
$current = git branch --show-current
if (-not $current) { throw '현재 브랜치를 확인할 수 없습니다.' }

Write-Host "==> Project: $ProjectRoot"
Write-Host "==> 현재 브랜치: $current"
Write-Host "==> 병합: ${GitRemote}/${GitBranch} -> $current"
Write-Host ''

git fetch $GitRemote $GitBranch
$counts = (git rev-list --left-right --count "HEAD...${GitRemote}/${GitBranch}") -split '\s+'
$behind = [int]$counts[1]

if ($behind -eq 0) {
    Write-Host "이미 ${GitRemote}/${GitBranch} 내용이 반영되어 있습니다."
    exit 0
}

& (Join-Path $ScriptDir 'compare-remote.ps1')
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ''
$confirm = Read-Host '위 내용으로 merge 하시겠습니까? (Y/N)'
if ($confirm -notmatch '^[Yy]$') {
    Write-Host '취소되었습니다.'
    exit 0
}

Write-Host "==> git merge ${GitRemote}/${GitBranch}"
git merge "${GitRemote}/${GitBranch}"
exit $LASTEXITCODE
