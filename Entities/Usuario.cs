namespace UsuariosApi.Entities;

public class Usuario
{
    public int Id { get; set; }

    public string Nombre { get; set; } = string.Empty;

    public string Apellido { get; set; } = string.Empty;

    public string Correo { get; set; } = string.Empty;

    // La contraseña NO se guarda en texto plano
    public string PasswordHash { get; set; } = string.Empty;

    // [4.4] Se eliminaron RolId y Edad de la entidad.
    // [4.4] FechaNacimiento reemplaza a Edad: la edad se calcula a partir de esta fecha
    public DateOnly FechaNacimiento { get; set; }

    // [4.4] Campo nuevo: Universidad
    public string Universidad { get; set; } = string.Empty;

    // [4.4] Campo nuevo: semestre actual del estudiante (1 a 10)
    public int Semestre { get; set; }

    public bool Activo { get; set; } = true;

    public DateTime FechaCreacion { get; set; } = DateTime.Now;
}
