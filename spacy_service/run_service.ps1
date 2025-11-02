# PowerShell script to run the spaCy NLP service
Write-Host "================================"
Write-Host "  spaCy NLP Microservice"
Write-Host "================================"
Write-Host ""

$serviceDir = Get-Location
$pythonExe = "$serviceDir\venv\Scripts\python.exe"
$appFile = "$serviceDir\app.py"

# Check if Python exists
if (-not (Test-Path $pythonExe)) {
    Write-Host "❌ ERROR: Python executable not found at $pythonExe"
    exit 1
}

Write-Host "✅ Python executable found"
Write-Host "📁 Service directory: $serviceDir"
Write-Host ""
Write-Host "Starting Flask service on http://localhost:5000..."
Write-Host ""
Write-Host "Press Ctrl+C to stop the service"
Write-Host "================================"
Write-Host ""

# Run Python with the app
& $pythonExe $appFile
