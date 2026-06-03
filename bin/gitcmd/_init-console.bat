@echo off
if defined GITCMD_CONSOLE_INIT exit /b 0
set "GITCMD_CONSOLE_INIT=1"
chcp 949 >nul 2>&1