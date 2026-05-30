# Detiene Jetty previo y arranca la aplicacion
$ErrorActionPreference = "Continue"
Set-Location $PSScriptRoot
. "$PSScriptRoot\load-config.ps1"

Write-Host "Deteniendo Jetty previo (si existe)..." -ForegroundColor Cyan
mvn -q jetty:stop 2>$null

Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue |
    ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }

Start-Sleep -Seconds 2
if (Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue) {
    Write-Host "El puerto 8080 sigue ocupado. Cierra la terminal donde corre Jetty o ejecuta: mvn jetty:stop" -ForegroundColor Red
    exit 1
}

Write-Host "Compilando..." -ForegroundColor Cyan
mvn -q compile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host "Arrancando en http://localhost:8080/BancoRestApi/" -ForegroundColor Green
Write-Host "Detener con Ctrl+C o en otra terminal: mvn jetty:stop" -ForegroundColor Yellow
mvn jetty:run
