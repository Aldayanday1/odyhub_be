@echo off
setlocal enabledelayedexpansion
echo =====================================================
echo        SISTEM PENGADUAN APPLICATION LAUNCHER
echo =====================================================

REM Cek apakah Maven tersedia
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven not found! Please install Maven or use IDE to run the application.
    echo.
    echo Alternative solutions:
    echo 1. Install Maven and add to PATH
    echo 2. Use your IDE (NetBeans/IntelliJ/Eclipse) to run the application
    echo 3. Use Spring Boot gradle wrapper if available
    echo.
    pause
    exit /b
)

REM Cek apakah port 8080 sedang digunakan
echo Checking if port 8080 is available...
netstat -ano | findstr :8080 >nul
if %errorlevel% == 0 (
    echo WARNING: Port 8080 is already in use!
    echo.
    echo Available options:
    echo 1. Kill existing process and use port 8080
    echo 2. Use alternative port 8081
    echo 3. Use alternative port 8082
    echo 4. Exit
    echo.
    set /p choice="Choose an option (1-4): "
    
    if "!choice!"=="1" (
        echo Stopping existing process on port 8080...
        for /f "tokens=5" %%i in ('netstat -ano ^| findstr :8080') do (
            taskkill /PID %%i /F >nul 2>&1
        )
        echo Process stopped. Starting application on port 8080...
        echo Using low memory settings for better performance...
        set MAVEN_OPTS=-Xms256m -Xmx1024m
        mvn spring-boot:run
    ) else if "!choice!"=="2" (
        echo Starting application on port 8081...
        set PORT=8081
        set MAVEN_OPTS=-Xms256m -Xmx1024m
        mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
    ) else if "!choice!"=="3" (
        echo Starting application on port 8082...
        set PORT=8082
        set MAVEN_OPTS=-Xms256m -Xmx1024m
        mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8082"
    ) else (
        echo Exiting...
        exit /b
    )
) else (
    echo Port 8080 is available. Starting application...
    echo Using optimized memory settings...
    set MAVEN_OPTS=-Xms256m -Xmx1024m
    mvn spring-boot:run
)

pause