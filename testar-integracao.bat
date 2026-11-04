@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"
if not exist "lib\mysql-connector-j.jar" (
    echo ERRO: coloque o MySQL Connector/J em lib\mysql-connector-j.jar.
    exit /b 1
)
if not exist build\classes mkdir build\classes
if not exist build\test-classes mkdir build\test-classes
(for /r src %%F in (*.java) do @(
    set "arquivoFonte=%%F"
    echo "!arquivoFonte:\=/!"
)) > build\sources.txt
javac -encoding UTF-8 -d build\classes @build\sources.txt
if errorlevel 1 exit /b 1
javac -encoding UTF-8 -cp "build\classes" -d build\test-classes test\br\com\debcontrol\test\MySqlCrudTest.java
if errorlevel 1 exit /b 1
java -cp "build\classes;build\test-classes;lib\mysql-connector-j.jar" br.com.debcontrol.test.MySqlCrudTest
exit /b %errorlevel%
