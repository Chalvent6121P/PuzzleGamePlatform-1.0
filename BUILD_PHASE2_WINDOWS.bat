@echo off
chcp 65001 >nul
cd /d "%~dp0"
echo [1/2] Maven clean package...
call mvn clean package
if errorlevel 1 (
    echo.
    echo BUILD FAILURE - 請確認 Maven、Java 11 與網路/本機 Maven repository。
    pause
    exit /b 1
)
echo.
echo [2/2] BUILD SUCCESS
echo JAR: target\PuzzleGamePlatform-Phase2.jar
 pause
