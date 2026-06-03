@echo off
pushd "%~dp0"
call _run-ps1.bat merge-remote-into-current.ps1
set ERR=%ERRORLEVEL%
popd
exit /b %ERR%
