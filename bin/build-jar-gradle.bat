@echo off
setlocal EnableExtensions

rem NSIGHT Message Management Service — Gradle bootJar
set "PROJECT_ROOT=%~dp0.."
cd /d "%PROJECT_ROOT%"

if not defined SKIP_TESTS set "SKIP_TESTS=true"
if not defined CLEAN set "CLEAN=false"

set "GRADLE_CMD=gradlew.bat"
if not exist "%GRADLE_CMD%" (
  echo ERROR: %GRADLE_CMD% not found. Run: gradle wrapper
  exit /b 1
)

set "TASK=bootJar"
if /i "%CLEAN%"=="true" set "TASK=clean %TASK%"
if /i "%SKIP_TESTS%"=="true" (
  call "%GRADLE_CMD%" %TASK% -x test --no-daemon
) else (
  call "%GRADLE_CMD%" %TASK% --no-daemon
)
if errorlevel 1 exit /b 1

set "JAR=%CD%\build\libs\nsight-message-mgmt-service-1.0.0.jar"
if not exist "%JAR%" (
  for %%F in ("%CD%\build\libs\nsight-message-mgmt-service-*.jar") do (
    echo %%~nxF | findstr /i "\.original$" >nul || set "JAR=%%~fF"
  )
)

if not exist "%JAR%" (
  echo ERROR: executable JAR not found under build\libs\
  exit /b 1
)

echo ==^> Built: %JAR%
dir "%JAR%"
endlocal
