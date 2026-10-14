-- =====================================================================
-- Actividad UCompensar - Android Kotlin + API REST .NET
-- Crea la base de datos y la tabla Usuarios en su estado ORIGINAL
-- (antes de los cambios de la actividad).
--
-- Úselo SOLO si todavía no tiene la tabla creada. La estructura se
-- reconstruyó a partir de la entidad Usuario del proyecto original.
--
-- Orden de ejecución:
--   1) Este script (00)                      -> estructura original
--   2) 01_actualizar_tabla_usuarios.sql      -> estructura nueva
--
-- Motor: MySQL 8+
-- Reemplace TU_BASE_DATOS por el nombre que use en appsettings.json.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS TU_BASE_DATOS
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE TU_BASE_DATOS;

CREATE TABLE IF NOT EXISTS Usuarios (
    Id            INT          NOT NULL AUTO_INCREMENT,
    Nombre        VARCHAR(100) NOT NULL,
    Apellido      VARCHAR(100) NOT NULL,
    Correo        VARCHAR(150) NOT NULL,
    PasswordHash  LONGTEXT     NOT NULL,
    Edad          INT          NOT NULL,
    Activo        TINYINT(1)   NOT NULL DEFAULT 1,
    RolId         INT          NOT NULL,
    FechaCreacion DATETIME(6)  NOT NULL,
    PRIMARY KEY (Id),
    UNIQUE KEY IX_Usuarios_Correo (Correo)
);

-- Evidencia "antes" de los cambios: debe mostrar Edad y RolId.
DESCRIBE Usuarios;
