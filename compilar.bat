@echo off
rem Compila o sistema em build\classes. Com --testes, compila tambem build\test-classes.
setlocal EnableDelayedExpansion
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo ERRO: javac nao encontrado. Instale o JDK 23 ou superior e configure o PATH.
    exit /b 1
)

if not exist build\classes mkdir build\classes
(for /r src %%F in (*.java) do @(
    set "arquivoFonte=%%F"
    echo "!arquivoFonte:\=/!"
)) > build\sources.txt
javac -encoding UTF-8 --release 23 -d build\classes @build\sources.txt
if errorlevel 1 (
    echo Falha na compilacao do sistema.
    exit /b 1
)

if /i not "%~1"=="--testes" exit /b 0
if not exist build\test-classes mkdir build\test-classes
(for /r test %%F in (*.java) do @(
    set "arquivoFonte=%%F"
    echo "!arquivoFonte:\=/!"
)) > build\tests.txt
javac -encoding UTF-8 --release 23 -cp build\classes -d build\test-classes @build\tests.txt
if errorlevel 1 (
    echo Falha na compilacao dos testes.
    exit /b 1
)
exit /b 0
