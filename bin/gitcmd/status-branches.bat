@echo off
pushd "%~dp0"
call _run-ps1.bat status-branches.ps1
set ERR=%ERRORLEVEL%
popd
exit /b %ERR%
