@echo off
setlocal
cd /d "%~dp0"

if not exist "lib\mysql-connector-j.jar" (
    echo ERRO: coloque o MySQL Connector/J em lib\mysql-connector-j.jar
    pause
    exit /b 1
)

call "%~dp0compilar.bat"
if errorlevel 1 (
    pause
    exit /b 1
)

java -cp "build\classes;lib\mysql-connector-j.jar" br.com.debcontrol.Main %*
exit /b %errorlevel%
