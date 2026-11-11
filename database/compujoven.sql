-- CompuJoven - preparación de MySQL (Workbench o terminal)
-- Ejecútalo UNA vez con un usuario administrador de MySQL (root).
-- Crea la base de datos y el usuario que usa la aplicación, de modo que la
-- app conecte igual en cualquier computador sin tocar application.properties.

CREATE DATABASE IF NOT EXISTS compujoven
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usuario de la aplicación (solo desarrollo)
CREATE USER IF NOT EXISTS 'compujoven'@'localhost'  IDENTIFIED BY 'compujoven123';
CREATE USER IF NOT EXISTS 'compujoven'@'127.0.0.1'  IDENTIFIED BY 'compujoven123';
GRANT ALL PRIVILEGES ON compujoven.* TO 'compujoven'@'localhost';
GRANT ALL PRIVILEGES ON compujoven.* TO 'compujoven'@'127.0.0.1';
FLUSH PRIVILEGES;

USE compujoven;

CREATE TABLE IF NOT EXISTS usuarios (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  nombre           VARCHAR(100) NOT NULL,
  telefono         VARCHAR(20),
  tipo_documento   VARCHAR(20),
  numero_documento VARCHAR(20),
  edad             INT          NOT NULL,
  email            VARCHAR(150) NOT NULL,
  password         VARCHAR(100) NOT NULL,   -- hash BCrypt
  rol              VARCHAR(20)  NOT NULL,   -- ADMINISTRADOR | JOVEN
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS formaciones (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  nombre      VARCHAR(150) NOT NULL,
  descripcion VARCHAR(1000),
  duracion    VARCHAR(50),
  requisitos  VARCHAR(1000),
  capacidad   INT          NOT NULL DEFAULT 0,   -- 0 = sin límite de cupos
  abierta     BIT          NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS postulaciones (
  id                BIGINT      NOT NULL AUTO_INCREMENT,
  usuario_id        BIGINT      NOT NULL,
  formacion_id      BIGINT      NOT NULL,
  fecha_postulacion DATETIME(6) NOT NULL,
  estado            VARCHAR(20) NOT NULL,   -- PENDIENTE | ACEPTADA | RECHAZADA
  PRIMARY KEY (id),
  UNIQUE KEY uk_postulacion (usuario_id, formacion_id),
  CONSTRAINT fk_post_usuario   FOREIGN KEY (usuario_id)   REFERENCES usuarios (id),
  CONSTRAINT fk_post_formacion FOREIGN KEY (formacion_id) REFERENCES formaciones (id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS hojas_de_vida (
  id             BIGINT       NOT NULL AUTO_INCREMENT,
  usuario_id     BIGINT       NOT NULL,
  nombre_archivo VARCHAR(255) NOT NULL,
  tipo_contenido VARCHAR(100) NOT NULL,   -- application/pdf | ...wordprocessingml.document
  tamano         BIGINT       NOT NULL,
  datos          LONGBLOB     NOT NULL,   -- contenido del archivo (máx. 5 MB)
  fecha_carga    DATETIME(6)  NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_hoja_usuario (usuario_id),
  CONSTRAINT fk_hoja_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Los datos de ejemplo (formaciones, jóvenes y postulaciones) y el administrador
-- los crea la propia aplicación en cada arranque si faltan; no hace falta insertarlos aquí.
