@echo off
rem Sistema completo em um banco temporario, removido ao final. Use --gui para testar os botoes.
setlocal
cd /d "%~dp0"
chcp 65001 >nul
if not exist "lib\mysql-connector-j.jar" (
    echo ERRO: driver JDBC ausente em lib\mysql-connector-j.jar.
    exit /b 1
)
call "%~dp0compilar.bat" --testes
if errorlevel 1 exit /b 1
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp "build\classes;build\test-classes;lib\mysql-connector-j.jar" br.com.debcontrol.test.SistemaIntegracaoTest %*
exit /b %errorlevel%
