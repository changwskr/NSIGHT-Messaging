#Requires -Version 5.1
$OutputEncoding = [Console]::OutputEncoding = [Text.UTF8Encoding]::UTF8
$ErrorActionPreference = 'Stop'

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = (Resolve-Path (Join-Path $ScriptDir '..\..')).Path
$GitRemote = if ($env:GIT_REMOTE) { $env:GIT_REMOTE } else { 'origin' }
$GitBranch = if ($env:GIT_BRANCH) { $env:GIT_BRANCH } else { 'master' }

Set-Location $ProjectRoot

Write-Host "==> Project: $ProjectRoot"
Write-Host "==> 비교: HEAD vs ${GitRemote}/${GitBranch}"
Write-Host ''

Write-Host "==> git fetch ${GitRemote} ${GitBranch}"
git fetch $GitRemote $GitBranch

$counts = (git rev-list --left-right --count "HEAD...${GitRemote}/${GitBranch}") -split '\s+'
$ahead = [int]$counts[0]
$behind = [int]$counts[1]

Write-Host ''
Write-Host "[커밋] HEAD에만 있음(앞섬): $ahead  /  ${GitRemote}/${GitBranch}에만 있음(뒤처짐): $behind"
Write-Host ''

if ($behind -eq 0) {
    Write-Host 'pull/merge로 받을 커밋이 없습니다.'
    exit 0
}

Write-Host "==> 받아올 커밋 목록 (HEAD..${GitRemote}/${GitBranch})"
git log --oneline "HEAD..${GitRemote}/${GitBranch}"
Write-Host ''

$files = @(git diff --name-only "HEAD...${GitRemote}/${GitBranch}")
Write-Host "[파일] 변경/추가 대상 파일 수: $($files.Count)"
Write-Host ''
Write-Host '==> diff --stat'
git diff --stat "HEAD...${GitRemote}/${GitBranch}"
Write-Host ''
Write-Host '==> 변경 파일 목록'
$files | ForEach-Object { Write-Host $_ }
Write-Host ''
Write-Host 'merge: merge-remote-into-current.bat'
Write-Host 'pull:  pull-remote.bat'
