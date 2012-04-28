@echo off
@set PATH=%PATH%;C:\Program Files\Java\jdk1.7.0\bin

@echo Kompilujem...
javac -Xlint:deprecation *.java

taskkill /F /FI "MODULES eq npjp2.dll"

