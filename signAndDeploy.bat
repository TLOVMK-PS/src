:: signAndDeploy.bat by Matt Fritz
:: November 13, 2010
:: Deploy either the client or server executable to the server directory
:: Usage: signAndDeploy [--client | --server]

@echo off

:: Check to make sure that a parameter was specified
if "%1" == "" goto :noParameter

:: Check to see if the parameter was either "--client" or "--server"
if "%1" == "--client" goto :deployClient
if "%1" == "--server" goto :deployServer

:: If the user specified anything else, tell him he's wrong
if not "%1" == "--client" if not "%1" == "--server" goto :noParameter

:: ===================================
:: DEPLOY THE CLIENT
:: ===================================

:deployClient

echo:
echo Signing the JAR file...

:: Sign the JAR file with the jarsigner utility
jarsigner -storepass buttknocker -signedjar HawksVMK_s.jar HawksVMK.jar HVMK

echo:
echo Deploying the signed JAR file to the server...
echo:

:: Copy the signed JAR file to the server directory
copy /V /Y HawksVMK_s.jar V:\game\HawksVMK_s.jar

echo: 
echo Signed JAR deployed.

goto :end

:: ===================================
:: DEPLOY THE SERVER
:: ===================================

:deployServer

echo:
echo Deploying server JAR file...
echo:

:: Copy the JAR file to the server directory
copy /V /Y HawksVMKServer.jar V:\game\HawksVMKServer.jar

echo:
echo Server JAR deployed.

goto :end

:: ===================================
:: NO PARAMETER/WRONG PARAMETER
:: ===================================

:noParameter

echo:
echo Usage: signAndDeploy [--client] [--server]
echo Please specify either --client or --server

goto :end

:: ===================================
:: TERMINATE EXECUTION
:: ===================================

:end