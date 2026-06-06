@echo off
setlocal EnableExtensions
chcp 65001 >nul 2>&1

set "BIN_DIR=%~dp0"
set "PROJECT_ROOT=%BIN_DIR%.."
cd /d "%PROJECT_ROOT%"

echo ==^> Project: %CD%
echo ==^> Remove Gradle build/ (Maven classpath only)
if exist "%PROJECT_ROOT%\build" (
  rmdir /s /q "%PROJECT_ROOT%\build"
  echo     deleted build/
)

echo ==^> mvn spring-boot:run
call mvn spring-boot:run -DskipTests
exit /b %ERRORLEVEL%
