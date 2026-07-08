@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist "target\PuzzleGamePlatform-Phase2.jar" (
    echo 找不到 target\PuzzleGamePlatform-Phase2.jar
    echo 請先執行 BUILD_PHASE2_WINDOWS.bat
    pause
    exit /b 1
)
java -jar "target\PuzzleGamePlatform-Phase2.jar"
if errorlevel 1 pause
