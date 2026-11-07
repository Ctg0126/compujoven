-- CompuJoven - script para MySQL Workbench
-- Opcional: la app crea/actualiza las tablas sola (spring.jpa.hibernate.ddl-auto=update).
-- Úsalo si prefieres crear la base de datos manualmente o revisar el esquema.

CREATE DATABASE IF NOT EXISTS compujoven
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
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
