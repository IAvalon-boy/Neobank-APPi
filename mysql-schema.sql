-- =============================================================================
-- Neobank-APPi | Script MySQL (Spring MVC + Jersey REST + Hibernate)
-- =============================================================================
-- Conexion por defecto: localhost:3306 | usuario: root | sin contraseña
-- Si tu MySQL usa clave, copia config.local.ps1.example -> config.local.ps1
--
-- PASOS PARA OTRO DESARROLLADOR (Windows):
--   1. git clone <repo>
--   2. (Opcional) config.local.ps1 con tu contraseña MySQL
--   3. .\setup-db.ps1
--   4. .\iniciar.ps1
--
-- Linux/macOS: config.local.sh.example -> config.local.sh y ./setup-db.sh
-- =============================================================================

CREATE DATABASE IF NOT EXISTS banco_neobank
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE banco_neobank;

DROP TABLE IF EXISTS cuenta;
DROP TABLE IF EXISTS cliente;

CREATE TABLE cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dui VARCHAR(10) NOT NULL,
    nombres VARCHAR(150) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_dui (dui)
) ENGINE=InnoDB;

CREATE TABLE cuenta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    numero_cuenta VARCHAR(20) NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cuenta_numero (numero_cuenta),
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
) ENGINE=InnoDB;

INSERT INTO cliente (dui, nombres) VALUES ('12345678-9', 'Cliente Demo');

INSERT INTO cuenta (id_cliente, numero_cuenta, saldo)
SELECT c.id, '000-000001-00', 500.00
FROM cliente c WHERE c.dui = '12345678-9' LIMIT 1;

INSERT INTO cuenta (id_cliente, numero_cuenta, saldo)
SELECT c.id, '000-000002-00', 120.50
FROM cliente c WHERE c.dui = '12345678-9' LIMIT 1;
