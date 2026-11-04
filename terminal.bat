@echo off
setlocal
cd /d "%~dp0"
call "%~dp0compilar-e-executar.bat" --terminal
if errorlevel 1 pause
endlocal
