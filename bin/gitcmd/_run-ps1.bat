@echo off
chcp 65001 >nul 2>&1
if not exist "%~1" (
  echo ERROR: not found: %~1
  exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~1"
exit /b %ERRORLEVEL%
