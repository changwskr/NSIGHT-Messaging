@echo off
setlocal EnableExtensions
chcp 65001 >nul 2>&1

set "BIN_DIR=%~dp0"
set "PROJECT_ROOT=%BIN_DIR%.."
cd /d "%PROJECT_ROOT%"

echo ==^> Project: %CD%
echo ==^> Maven spring-boot:run (extension not required)

call mvn -q compile -DskipTests
if errorlevel 1 (
  echo ==^> compile FAILED
  exit /b 1
)

call mvn spring-boot:run -DskipTests "-Dspring-boot.run.jvmArguments=-Dfile.encoding=UTF-8 -Dspring.profiles.active=local"
exit /b %ERRORLEVEL%
