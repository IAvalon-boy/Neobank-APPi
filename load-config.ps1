# Carga config.local.ps1 (si existe) y exporta variables para scripts y Java.
$script:ConfigRoot = if ($PSScriptRoot) { $PSScriptRoot } else { Split-Path -Parent $MyInvocation.MyCommand.Path }

$script:MYSQL_USER = "root"
$script:MYSQL_PASSWORD = ""
$script:MYSQL_HOST = "localhost"
$script:MYSQL_PORT = "3306"

$localConfig = Join-Path $script:ConfigRoot "config.local.ps1"
if (Test-Path $localConfig) {
    . $localConfig
    Write-Host "Config local cargada: $localConfig" -ForegroundColor DarkGray
} else {
    Write-Host "Sin config.local.ps1 (usa valores por defecto: root sin clave)." -ForegroundColor DarkGray
    Write-Host "Copia config.local.ps1.example -> config.local.ps1 si tu MySQL tiene contraseña." -ForegroundColor Yellow
}

$env:MYSQL_USER = $script:MYSQL_USER
$env:MYSQL_HOST = $script:MYSQL_HOST
$env:MYSQL_PORT = $script:MYSQL_PORT

if ($script:MYSQL_PASSWORD) {
    $env:MYSQL_PASSWORD = $script:MYSQL_PASSWORD
} else {
    Remove-Item Env:MYSQL_PASSWORD -ErrorAction SilentlyContinue
}
