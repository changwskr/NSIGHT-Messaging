@echo off
setlocal EnableExtensions

rem 원격 develop 최신 반영 후 build-jar.bat 실행
set "BIN_DIR=%~dp0"
set "PROJECT_ROOT=%BIN_DIR%.."
cd /d "%PROJECT_ROOT%"

set "GIT_BRANCH=develop"

echo ==^> Project: %CD%
echo ==^> git fetch origin %GIT_BRANCH%
git fetch origin %GIT_BRANCH%
if errorlevel 1 exit /b 1

git rev-parse --verify %GIT_BRANCH% >nul 2>&1
if errorlevel 1 (
  echo ==^> git checkout -b %GIT_BRANCH% origin/%GIT_BRANCH%
  git checkout -b %GIT_BRANCH% origin/%GIT_BRANCH%
) else (
  git checkout %GIT_BRANCH%
)
if errorlevel 1 exit /b 1

echo ==^> git pull --ff-only origin %GIT_BRANCH%
git pull --ff-only origin %GIT_BRANCH%
if errorlevel 1 exit /b 1

echo ==^> build-jar.bat
call "%BIN_DIR%build-jar.bat"
exit /b %ERRORLEVEL%
