@echo off
echo ========================================
echo   SECURITY CHECK - Pre-Push Validation
echo ========================================
echo.

set ERROR_FOUND=0

echo [1/5] Checking for sensitive files...
echo.

REM Check if .env is in staging
git diff --cached --name-only | findstr /C:".env" >nul
if %errorlevel% == 0 (
    echo [ERROR] .env file is staged for commit!
    echo         Run: git reset HEAD .env
    set ERROR_FOUND=1
) else (
    echo [OK] .env not staged
)

REM Check for log files
git diff --cached --name-only | findstr /C:".log" >nul
if %errorlevel% == 0 (
    echo [WARNING] Log files found in commit
    set ERROR_FOUND=1
) else (
    echo [OK] No log files
)

echo.
echo [2/5] Checking application.properties...
echo.

REM Check if application.properties contains actual passwords
findstr /C:"password=Unkown007" "src\main\resources\application.properties" >nul 2>&1
if %errorlevel% == 0 (
    echo [ERROR] Hardcoded password found in application.properties!
    echo         Replace with: ${DB_PASSWORD:your_password_here}
    set ERROR_FOUND=1
) else (
    echo [OK] No hardcoded passwords
)

REM Check for actual email
findstr /C:"onlymarfa69@gmail.com" "src\main\resources\application.properties" >nul 2>&1
if %errorlevel% == 0 (
    echo [ERROR] Actual email found in application.properties!
    echo         Replace with: ${MAIL_USERNAME:your-email@gmail.com}
    set ERROR_FOUND=1
) else (
    echo [OK] No actual email addresses
)

echo.
echo [3/5] Checking for credentials in code...
echo.

REM Search for potential passwords in Java files
findstr /S /I /C:"password" /C:"secret" /C:"api_key" "src\main\java\*.java" | findstr /V /C:"Password" /C:"password)" >nul 2>&1
if %errorlevel% == 0 (
    echo [WARNING] Found potential credentials in Java files
    echo            Please review manually
) else (
    echo [OK] No obvious credentials in code
)

echo.
echo [4/5] Verifying .gitignore...
echo.

if exist ".gitignore" (
    findstr /C:".env" ".gitignore" >nul 2>&1
    if %errorlevel% == 0 (
        echo [OK] .env is in .gitignore
    ) else (
        echo [ERROR] .env not found in .gitignore!
        set ERROR_FOUND=1
    )
    
    findstr /C:"*.log" ".gitignore" >nul 2>&1
    if %errorlevel% == 0 (
        echo [OK] Log files in .gitignore
    ) else (
        echo [WARNING] Log files not in .gitignore
    )
) else (
    echo [ERROR] .gitignore file not found!
    set ERROR_FOUND=1
)

echo.
echo [5/5] Checking required template files...
echo.

if exist ".env.example" (
    echo [OK] .env.example exists
) else (
    echo [WARNING] .env.example not found
)

if exist "SECURITY_GUIDE.md" (
    echo [OK] SECURITY_GUIDE.md exists
) else (
    echo [WARNING] SECURITY_GUIDE.md not found
)

if exist "src\main\resources\application.properties.example" (
    echo [OK] application.properties.example exists
) else (
    echo [WARNING] application.properties.example not found
)

echo.
echo ========================================
echo   VALIDATION SUMMARY
echo ========================================

if %ERROR_FOUND% == 1 (
    echo.
    echo [FAILED] Errors found! Please fix before pushing.
    echo.
    echo Next steps:
    echo 1. Fix the errors mentioned above
    echo 2. Run this check again
    echo 3. Only push when all checks pass
    echo.
    pause
    exit /b 1
) else (
    echo.
    echo [SUCCESS] All security checks passed!
    echo You are safe to push to GitHub.
    echo.
    echo Don't forget to:
    echo - Keep your .env file LOCAL only
    echo - Never commit actual credentials
    echo - Review SECURITY_GUIDE.md regularly
    echo.
    pause
    exit /b 0
)
