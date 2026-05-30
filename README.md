# Neobank-APPi

## Requisitos

- JDK 8+
- Maven
- MySQL en `localhost:3306`

## 1. Clonar

```powershell
git clone <URL-DEL-REPO>
cd Neobank-APPi
```

## 2. Conexión a MySQL

Por defecto: `root` / sin contraseña / `localhost:3306` / base `banco_neobank`.

Si tu MySQL tiene contraseña:

```powershell
Copy-Item config.local.ps1.example config.local.ps1
```

Editar `config.local.ps1`:

```powershell
$script:MYSQL_USER = "root"
$script:MYSQL_PASSWORD = "tu_clave"
$script:MYSQL_HOST = "localhost"
$script:MYSQL_PORT = "3306"
```

> `config.local.ps1` no se sube a Git.

## 3. Crear la base

```powershell
.\setup-db.ps1
```

## 4. Ejecutar

```powershell
mvn jetty:run
```

Si el puerto 8080 está ocupado:

```powershell
mvn jetty:stop
mvn jetty:run
```

O con el script:

```powershell
.\iniciar.ps1
```

Abrir: **http://localhost:8080/BancoRestApi/**

## Datos de prueba

- DUI: `12345678-9`
- Cuentas: `000-000001-00`, `000-000002-00`
