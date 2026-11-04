@echo off
rem Testes locais: nao precisam do MySQL nem do driver.
setlocal
cd /d "%~dp0"
chcp 65001 >nul

call "%~dp0compilar.bat" --testes
if errorlevel 1 exit /b 1

java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp "build\classes;build\test-classes" br.com.debcontrol.test.TestRunner
set "RESULTADO=%errorlevel%"
pause
exit /b %RESULTADO%
