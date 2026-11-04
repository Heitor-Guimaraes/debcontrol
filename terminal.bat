@echo off
setlocal
cd /d "%~dp0"
call compilar-e-executar.bat --terminal
if errorlevel 1 pause
endlocal
