@echo off
setlocal EnableExtensions

rem NSIGHT Message Management Service — Maven compile + executable JAR package
set "PROJECT_ROOT=%~dp0.."
cd /d "%PROJECT_ROOT%"

if not defined SKIP_TESTS set "SKIP_TESTS=true"
if not defined CLEAN set "CLEAN=false"

set "MVN_GOAL=package -B"
if /i "%CLEAN%"=="true" set "MVN_GOAL=clean %MVN_GOAL%"
if /i "%SKIP_TESTS%"=="true" set "MVN_GOAL=%MVN_GOAL% -DskipTests"

if not defined MAVEN_OPTS set "MAVEN_OPTS=-Dfile.encoding=UTF-8"

echo ==^> Project: %CD%
echo ==^> mvn %MVN_GOAL%
call mvn %MVN_GOAL%
if errorlevel 1 exit /b 1

set "JAR=%CD%\target\nsight-message-mgmt-service-1.0.0.jar"
if not exist "%JAR%" (
  for %%F in ("%CD%\target\nsight-message-mgmt-service-*.jar") do (
    echo %%~nxF | findstr /i "\.original$" >nul || set "JAR=%%~fF"
  )
)

if not exist "%JAR%" (
  echo ERROR: executable JAR not found under target\
  exit /b 1
)

echo ==^> Built: %JAR%
dir "%JAR%"
endlocal
