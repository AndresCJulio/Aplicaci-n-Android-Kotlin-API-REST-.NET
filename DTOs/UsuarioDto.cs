namespace UsuariosApi.DTOs;

// [4.4] DTO de salida: se eliminó Edad; se agregaron FechaNacimiento,
// Universidad y Semestre. Nunca incluye la contraseña ni su hash.
public class UsuarioDto
{
    public int Id { get; set; }

    public string Nombre { get; set; } = string.Empty;

    public string Apellido { get; set; } = string.Empty;

    public string Correo { get; set; } = string.Empty;

    public DateOnly FechaNacimiento { get; set; }

    public string Universidad { get; set; } = string.Empty;

    public int Semestre { get; set; }

    public bool Activo { get; set; }

    public DateTime FechaCreacion { get; set; }
}
