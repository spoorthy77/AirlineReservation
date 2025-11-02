@echo off
REM Run spaCy NLP Flask Service
echo.
echo ========================================
echo  spaCy NLP Microservice
echo ========================================
echo.

cd /d "C:\Users\m6793\OneDrive\Pictures\Documents\NetBeansProjects\AirlineReservation\spacy_service"

echo Activating Python virtual environment...
call venv\Scripts\activate.bat

echo.
echo Starting Flask service on http://localhost:5000...
echo.
echo Press Ctrl+C to stop
echo.
python app.py
