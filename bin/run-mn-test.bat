@echo off

setlocal EnableExtensions



if /i "%~1"=="/?" goto usage

if /i "%~1"=="--help" goto usage

if /i "%~1"=="-h" goto usage

if /i "%~1"=="help" goto usage



pushd "%~dp0.."

if errorlevel 1 (

    echo ERROR: cannot change to project root.

    exit /b 1

)



where mvn >nul 2>&1

if errorlevel 1 (

    echo ERROR: mvn not found in PATH.

    popd

    exit /b 1

)



if not exist pom.xml (

    echo ERROR: pom.xml not found.

    popd

    exit /b 1

)



set MAIN_CLASS=com.nh.nsight.messaging.zpilotfwk.mn.MN_SP_COMMON

echo [MN_SP_COMMON] main class: %MAIN_CLASS%



if "%~1"=="" (

    echo [MN_SP_COMMON] mode: container

    call mvn -q compile -DskipTests exec:java@mn-test "-Dexec.args=container"

) else (

    echo [MN_SP_COMMON] mode: %*

    call mvn -q compile -DskipTests exec:java@mn-test "-Dexec.args=%*"

)

set ERR=%ERRORLEVEL%

popd

exit /b %ERR%



:usage

echo.

echo Usage: run-mn-test.bat [container^|usertransaction]

echo   run-mn-test.bat

echo   run-mn-test.bat container

echo   run-mn-test.bat usertransaction

echo.

exit /b 0

