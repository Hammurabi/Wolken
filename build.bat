@echo off

rem download python if it does not exist
:: Check for Python Installation
python --version 2>NUL
if errorlevel 1 goto errorNoPython
echo python is installed

java -version 2>NUL
if errorlevel 1 goto errorNoJava
echo java is installed

python scripts/build.py
pause

:: Reaching here means Python is installed.
:: Execute stuff...

:: Once done, exit the batch file -- skips executing the errorNoPython section
goto:eof

:errorNoPython
echo.

echo Error^: python not installed
echo please install the latest version from https://www.python.org/downloads/

:errorNoJava
echo.

echo Error^: java not installed
echo please install the latest version from https://www.java.com/en/download/
pause