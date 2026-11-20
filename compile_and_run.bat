@echo off
echo Compiling Airline Reservation System...
javac -cp . -encoding UTF-8 Start.java DataInitializer.java Entities_All\*.java FileManagement\*.java GUI\*.java
if %ERRORLEVEL% NEQ 0 (
    echo COMPILATION FAILED
    pause
    exit /b 1
)
echo Compilation successful! Starting application...
java -cp . Start
