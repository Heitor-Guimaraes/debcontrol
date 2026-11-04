@echo off
rem CRUD real no banco configurado: grava e remove um cliente temporario.
setlocal
cd /d "%~dp0"
if not exist "lib\mysql-connector-j.jar" (
    echo ERRO: coloque o MySQL Connector/J em lib\mysql-connector-j.jar.
    exit /b 1
)
call "%~dp0compilar.bat" --testes
if errorlevel 1 exit /b 1
java -cp "build\classes;build\test-classes;lib\mysql-connector-j.jar" br.com.debcontrol.test.MySqlCrudTest
exit /b %errorlevel%
