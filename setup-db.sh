#!/usr/bin/env bash
# Carga mysql-schema.sql en MySQL (Linux / macOS / Git Bash)
set -euo pipefail
cd "$(dirname "$0")"

MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-}"
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"

if [[ -f config.local.sh ]]; then
  # shellcheck source=/dev/null
  source config.local.sh
  echo "Config local cargada: config.local.sh"
fi

SQL_FILE="mysql-schema.sql"

run_mysql() {
  if [[ -n "$MYSQL_PASSWORD" ]]; then
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" -h "$MYSQL_HOST" -P "$MYSQL_PORT" < "$SQL_FILE"
  else
    mysql -u "$MYSQL_USER" -h "$MYSQL_HOST" -P "$MYSQL_PORT" < "$SQL_FILE"
  fi
}

if command -v docker >/dev/null 2>&1; then
  CONTAINER=$(docker ps --filter "publish=${MYSQL_PORT}" --format '{{.Names}}' | head -n 1 || true)
  if [[ -n "$CONTAINER" ]]; then
    echo "Ejecutando script en contenedor Docker: $CONTAINER"
    if [[ -n "$MYSQL_PASSWORD" ]]; then
      docker exec -i "$CONTAINER" mysql -u "$MYSQL_USER" -p"$MYSQL_PASSWORD" < "$SQL_FILE"
    else
      docker exec -i "$CONTAINER" mysql -u "$MYSQL_USER" < "$SQL_FILE"
    fi
    echo "Base de datos lista: banco_neobank"
    exit 0
  fi
fi

if command -v mysql >/dev/null 2>&1; then
  run_mysql
  echo "Base de datos lista: banco_neobank"
  exit 0
fi

echo "No se encontro mysql ni contenedor Docker en el puerto $MYSQL_PORT."
echo "Copia config.local.sh.example -> config.local.sh si necesitas contraseña."
exit 1
