@echo off
setlocal EnableDelayedExpansion
cd /d "%~dp0"

if not exist build\classes mkdir build\classes
if not exist build\test-classes mkdir build\test-classes

(for /r src %%F in (*.java) do @(
    set "arquivoFonte=%%F"
    echo "!arquivoFonte:\=/!"
)) > build\sources.txt
javac -encoding UTF-8 -d build\classes @build\sources.txt
if errorlevel 1 exit /b 1

(for /r test %%F in (*.java) do @(
    set "arquivoFonte=%%F"
    echo "!arquivoFonte:\=/!"
)) > build\tests.txt
javac -encoding UTF-8 -cp "build\classes" -d build\test-classes @build\tests.txt
if errorlevel 1 exit /b 1

java -cp "build\classes;build\test-classes" br.com.debcontrol.test.TestRunner
pause
endlocal
