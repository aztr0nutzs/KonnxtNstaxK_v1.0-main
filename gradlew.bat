@echo off
setlocal
set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_HOME=%DIRNAME%
set APP_BASE_NAME=%~n0

if not "%JAVA_HOME%"=="" (
    set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
    set JAVA_EXE=java
)

set WRAPPER_JAR=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar
set WRAPPER_SHARED=%APP_HOME%\gradle\wrapper\gradle-wrapper-shared.jar
set CLASSPATH=%WRAPPER_JAR%;%WRAPPER_SHARED%
"%JAVA_EXE%" -Xmx64m -Xms64m -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
