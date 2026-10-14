-- =====================================================================
-- Actividad UCompensar - Android Kotlin + API REST .NET
-- [4.4] Actualiza la tabla Usuarios para soportar:
--   * FechaNacimiento (reemplaza a Edad)
--   * Universidad
--   * Semestre
-- y elimina la columna RolId.
--
-- Motor: MySQL 8+
-- IMPORTANTE: reemplace TU_BASE_DATOS por el nombre de su base de datos
-- (el mismo que usa en appsettings.json) y ejecute el script completo.
-- Se recomienda hacer un respaldo antes de ejecutarlo.
-- =====================================================================

USE TU_BASE_DATOS;

-- 1) Agregar las columnas nuevas.
--    Se crean con un valor por defecto temporal para que los usuarios
--    que ya existen no rompan la restricción NOT NULL.
ALTER TABLE Usuarios
    ADD COLUMN FechaNacimiento DATE         NOT NULL DEFAULT '2000-01-01',
    ADD COLUMN Universidad     VARCHAR(150) NOT NULL DEFAULT '',
    ADD COLUMN Semestre        INT          NOT NULL DEFAULT 1;

-- 2) Conservar una fecha aproximada para los usuarios existentes,
--    calculada a partir de la edad que tenían registrada.
UPDATE Usuarios
SET FechaNacimiento = DATE_SUB(CURDATE(), INTERVAL Edad YEAR)
WHERE Edad IS NOT NULL AND Edad > 0;

-- 3) Eliminar las columnas que ya no se usan.
ALTER TABLE Usuarios
    DROP COLUMN Edad,
    DROP COLUMN RolId;

-- 4) Quitar los valores por defecto temporales: a partir de ahora la API
--    siempre debe enviar estos datos al registrar un usuario.
ALTER TABLE Usuarios
    ALTER COLUMN FechaNacimiento DROP DEFAULT,
    ALTER COLUMN Universidad     DROP DEFAULT,
    ALTER COLUMN Semestre        DROP DEFAULT;

-- 5) Verificación: debe mostrar FechaNacimiento, Universidad y Semestre,
--    y NO debe mostrar Edad ni RolId.
DESCRIBE Usuarios;
