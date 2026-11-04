@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

if not exist "lib\mysql-connector-j.jar" (
    echo ERRO: coloque o MySQL Connector/J em lib\mysql-connector-j.jar
    pause
    exit /b 1
)

if not exist build\classes mkdir build\classes
(for /r src %%F in (*.java) do @(
    set "arquivoFonte=%%F"
    echo "!arquivoFonte:\=/!"
)) > build\sources.txt
javac -encoding UTF-8 -cp "lib\mysql-connector-j.jar" -d build\classes @build\sources.txt

if errorlevel 1 (
    echo Falha na compilacao.
    pause
    exit /b 1
)

java -cp "build\classes;lib\mysql-connector-j.jar" br.com.debcontrol.Main %*
endlocal
