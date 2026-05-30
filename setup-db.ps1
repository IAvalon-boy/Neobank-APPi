# Carga mysql-schema.sql en MySQL (localhost:3306)
$ErrorActionPreference = "Stop"
. "$PSScriptRoot\load-config.ps1"

$sqlFile = Join-Path $PSScriptRoot "mysql-schema.sql"
$user = $script:MYSQL_USER
$pass = $script:MYSQL_PASSWORD
$hostName = $script:MYSQL_HOST
$port = $script:MYSQL_PORT

function Invoke-SqlFile {
    param([string]$MysqlExe, [string[]]$ExtraArgs)

    if ($pass) {
        & cmd /c "`"$MysqlExe`" -u $user -p$pass -h $hostName -P $port --protocol=TCP $ExtraArgs < `"$sqlFile`""
    } else {
        & cmd /c "`"$MysqlExe`" -u $user -h $hostName -P $port --protocol=TCP $ExtraArgs < `"$sqlFile`""
    }
    return $LASTEXITCODE
}

$docker = Get-Command docker -ErrorAction SilentlyContinue
if ($docker) {
    $container = docker ps --filter "publish=$port" --format "{{.Names}}" 2>$null | Select-Object -First 1
    if ($container) {
        Write-Host "Ejecutando script en contenedor Docker: $container" -ForegroundColor Cyan
        if ($pass) {
            Get-Content $sqlFile -Raw | docker exec -i $container mysql -u $user -p$pass
        } else {
            Get-Content $sqlFile -Raw | docker exec -i $container mysql -u $user
        }
        if ($LASTEXITCODE -eq 0) {
            Write-Host "Base de datos lista: banco_neobank" -ForegroundColor Green
            exit 0
        }
        Write-Host "Error al ejecutar SQL en Docker." -ForegroundColor Red
        exit $LASTEXITCODE
    }
}

$candidates = @(
    "mysql",
    "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
    "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
    "C:\xampp\mysql\bin\mysql.exe"
)

foreach ($path in $candidates) {
    $mysql = $null
    if ($path -eq "mysql") {
        $found = Get-Command mysql -ErrorAction SilentlyContinue
        if ($found) { $mysql = $found.Source }
    } elseif (Test-Path $path) {
        $mysql = $path
    }

    if ($mysql) {
        Write-Host "Usando cliente: $mysql" -ForegroundColor Cyan
        $code = Invoke-SqlFile -MysqlExe $mysql -ExtraArgs ""
        if ($code -eq 0) {
            Write-Host "Base de datos lista: banco_neobank" -ForegroundColor Green
            exit 0
        }
        Write-Host "Error al ejecutar el script SQL." -ForegroundColor Red
        exit $code
    }
}

Write-Host "No se encontro MySQL ni contenedor Docker en el puerto $port." -ForegroundColor Red
Write-Host "Enciende MySQL y vuelve a ejecutar: .\setup-db.ps1" -ForegroundColor Yellow
exit 1
