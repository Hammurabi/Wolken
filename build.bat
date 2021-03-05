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

/tools/maven/bin/mvn.cmd --version 2>NUL
if errorlevel 1 goto installMavenTask
pause

:: Reaching here means Python is installed.
:: Execute stuff...

:: Once done, exit the batch file -- skips executing the errorNoPython section
goto:eof

:errorNoPython
echo.

echo error^: python not installed
echo please install the latest version from https://www.python.org/downloads/

:errorNoJava
echo.

echo error^: java not installed
echo please install the latest version from https://www.java.com/en/download/
pause

:installMavenTask
python scripts/install_maven.py
goto buildTask