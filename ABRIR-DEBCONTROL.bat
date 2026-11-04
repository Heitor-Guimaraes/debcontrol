@echo off
setlocal
cd /d "%~dp0"
title debcontrol - Inicializacao
call "%~dp0compilar-e-executar.bat"
if errorlevel 1 (
    echo.
    echo Nao foi possivel iniciar o debcontrol. Confira a mensagem acima.
    pause
)
endlocal
