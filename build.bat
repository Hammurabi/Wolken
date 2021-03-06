@echo off

rem set the active directory
cd /D "%~dp0"

rem download python if it does not exist
:: Check for Python Installation
python --version 2>NUL
if errorlevel 1 goto errorNoPython
echo python is installed

java -version 2>NUL
if errorlevel 1 goto errorNoJava
echo java is installed

if exist "C:\Program Files\Java\" goto setJavaHomex64 else goto setJavaHomex86

:continue
echo set java (%arch%) path to %JAVA_HOME%
if not exist %~dp0tools\maven\bin\mvn.cmd goto installMavenTask
goto maven_installed

:maven_installed
echo maven is installed
goto buildTask
pause

:: Reaching here means Python is installed.
:: Execute stuff...

:: Once done, exit the batch file -- skips executing the errorNoPython section
goto:eof

:errorNoPython
echo.

echo error^: python not installed
echo please install the latest version from https://www.python.org/downloads/
pause

:errorNoJava
echo.

echo error^: java not installed
echo please install the latest version from https://www.java.com/en/download/
pause

:installMavenTask
python scripts/install_maven.py
goto maven_installed
pause

:buildTask
echo starting build
pause
%~dp0tools\maven\bin\mvn.cmd clean package
pause

:setJavaHomex64
set arch=64
for /d %%i in ("C:\Program Files\Java\*") do set JAVA_HOME=%%i
goto continue
pause
:setJavaHomex86
set arch=x86
for /d %%i in ("C:\Program Files (x86)\Java\*") do set JAVA_HOME=%%i
goto continue
pause
