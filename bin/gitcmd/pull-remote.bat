@echo off
call "%~dp0_init-console.bat"
setlocal EnableExtensions

set "BIN_DIR=%~dp0"
set "PROJECT_ROOT=%BIN_DIR%..\.."
cd /d "%PROJECT_ROOT%"

if not defined GIT_REMOTE set "GIT_REMOTE=origin"
if not defined GIT_BRANCH set "GIT_BRANCH=develop"

echo ==^> Project: %CD%
echo ==^> git fetch %GIT_REMOTE% %GIT_BRANCH%
git fetch %GIT_REMOTE% %GIT_BRANCH%
if errorlevel 1 exit /b 1

git rev-parse --verify %GIT_BRANCH% >nul 2>&1
if errorlevel 1 (
  echo ==^> git checkout -b %GIT_BRANCH% %GIT_REMOTE%/%GIT_BRANCH%
  git checkout -b %GIT_BRANCH% %GIT_REMOTE%/%GIT_BRANCH%
) else (
  git checkout %GIT_BRANCH%
)
if errorlevel 1 exit /b 1

echo ==^> git pull --ff-only %GIT_REMOTE% %GIT_BRANCH%
git pull --ff-only %GIT_REMOTE% %GIT_BRANCH%
exit /b %ERRORLEVEL%
