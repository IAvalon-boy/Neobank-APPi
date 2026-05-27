-- Base de datos alineada con las entidades Hibernate del proyecto Neobank-APPi
-- Ejecuta este script en MySQL ANTES de arrancar la aplicación.

CREATE DATABASE IF NOT EXISTS banco_neobank
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE banco_neobank;

CREATE TABLE IF NOT EXISTS cliente (
    id BIGINT NOT NULL AUTO_INCREMENT,
    dui VARCHAR(10) NOT NULL,
    nombres VARCHAR(150) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cliente_dui (dui)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS cuenta (
    id BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    numero_cuenta VARCHAR(20) NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id),
    UNIQUE KEY uk_cuenta_numero (numero_cuenta),
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id)
) ENGINE=InnoDB;

-- Datos de prueba (puedes volver a ejecutar el script: IGNORE evita duplicados)
INSERT IGNORE INTO cliente (dui, nombres) VALUES ('12345678-9', 'Cliente Demo');

INSERT IGNORE INTO cuenta (id_cliente, numero_cuenta, saldo)
SELECT c.id, '000-000001-00', 500.00
FROM cliente c
WHERE c.dui = '12345678-9'
LIMIT 1;

INSERT IGNORE INTO cuenta (id_cliente, numero_cuenta, saldo)
SELECT c.id, '000-000002-00', 120.50
FROM cliente c
WHERE c.dui = '12345678-9'
LIMIT 1;
