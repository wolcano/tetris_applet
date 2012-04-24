@echo off
@set PATH=%PATH%;C:\Program Files\Java\jdk1.7.0\bin

@echo Kompilujem...
javac -Xlint:deprecation *.java >vyrob_class.log 2>&1

