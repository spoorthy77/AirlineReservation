@echo off
REM spaCy NLP Service Quick Start Script for Windows PowerShell
REM This script sets up and runs the Python spaCy microservice

echo.
echo ========================================
echo  spaCy NLP Microservice Setup
echo ========================================
echo.

cd /d "C:\Users\m6793\OneDrive\Pictures\Documents\NetBeansProjects\AirlineReservation\spacy_service"

echo [1/4] Checking Python installation...
python --version
if errorlevel 1 (
    echo ❌ ERROR: Python not found! Please install Python 3.8+
    echo Visit: https://www.python.org/downloads/
    pause
    exit /b 1
)
echo ✅ Python found

echo.
echo [2/4] Creating virtual environment...
if not exist venv (
    python -m venv venv
    echo ✅ Virtual environment created
) else (
    echo ✅ Virtual environment already exists
)

echo.
echo [3/4] Activating virtual environment and installing dependencies...
call venv\Scripts\activate.bat

pip install -r requirements.txt
if errorlevel 1 (
    echo ❌ ERROR: Failed to install dependencies
    pause
    exit /b 1
)
echo ✅ Dependencies installed

echo.
echo [4/4] Downloading spaCy model...
python -m spacy download en_core_web_sm -q
if errorlevel 1 (
    echo ⚠️  WARNING: Could not download spaCy model automatically
    echo Attempting alternative download method...
    pip install https://github.com/explosion/spacy-models/releases/download/en_core_web_sm-3.7.1/en_core_web_sm-3.7.1-py3-none-any.whl
    if errorlevel 1 (
        echo ❌ ERROR: Failed to download spaCy model
        echo Please run manually: python -m spacy download en_core_web_sm
        pause
        exit /b 1
    )
)
echo ✅ spaCy model downloaded

echo.
echo ========================================
echo  Setup Complete! Starting Service...
echo ========================================
echo.
echo 📡 Service will run on: http://localhost:5000
echo.
echo 🚀 Starting Flask application...
echo.
python app.py
