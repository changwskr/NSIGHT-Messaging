@echo off
setlocal EnableExtensions
rem TpcUtil.main() — 경로에 ( ) 가 있으면 echo %%CD%% 사용 금지 — cmd 괄호 오류

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

echo [TpcUtil] ready
if "%~1"=="" (
    echo [TpcUtil] command: demo
    call mvn -q exec:java
) else (
    echo [TpcUtil] args: %*
    call mvn -q exec:java "-Dexec.args=%*"
)
set ERR=%ERRORLEVEL%
popd

if not "%ERR%"=="0" (
    echo.
    echo TpcUtil failed. Start server: mvn spring-boot:run
)
exit /b %ERR%

:usage
echo.
echo Usage: run-tpc-util.bat [list^|get ID^|create^|demo]
echo   run-tpc-util.bat
echo   run-tpc-util.bat list
echo   run-tpc-util.bat get 1
echo   run-tpc-util.bat create
echo Server: mvn spring-boot:run  ^(port 8080^)
echo.
exit /b 0
