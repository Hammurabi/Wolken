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

if "%JAVA_HOME%"=="" goto setJavaHome

:continue
if not exist %~dp0tools\maven\bin\mvn.cmd goto installMavenTask
goto maven_installed

:maven_installed
echo maven is installed
pause
goto buildTask
pause

pause
goto:eof

:errorNoPython
echo.

echo error^: python not installed
echo please install the latest version from https://www.python.org/downloads/
pause

:errorNoJava
echo.

echo error^: java not installed
echo please install the latest version from https://jdk.java.net/
pause

:installMavenTask
python scripts/install_maven.py
goto maven_installed
pause

:buildTask
echo starting build
rem run maven package task
%~dp0tools\maven\bin\mvn.cmd clean package
pause

:setJavaHomex64
set arch=64
for /d %%i in ("C:\Program Files\Java\jdk*") do set jdk=%%i
goto doSetJavaHome
pause

:setJavaHomex86
set arch=x86
for /d %%i in ("C:\Program Files (x86)\Java\jdk*") do set jdk=%%i
goto doSetJavaHome
pause

:setJavaHome
set /P check=JAVA_HOME is not set, this program can set it for you, however it needs to be run with administrator priviliges, continue? (y/n):
if "%check%"=="n" goto done
if exist "C:\Program Files\Java\" goto setJavaHomex64 else goto setJavaHomex86

:doSetJavaHome
if "%jdk%"=="" goto noJDK
setx /m JAVA_HOME "%jdk%"
if errorlevel 1 goto needAdminPrivs
else echo set java (%arch%) path to %JAVA_HOME%
goto continue

:noJDK
echo no java jdk was installed, please make sure you have a JDK installed and not a JRE
goto done

:needAdminPrivs
echo please run the program again with administrator priviliges
pause

:done
pause