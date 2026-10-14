using System.ComponentModel.DataAnnotations;

namespace UsuariosApi.DTOs;

// [4.4] Cambios: Edad se reemplazó por FechaNacimiento;
// se agregaron Universidad y Semestre.
public class ActualizarUsuarioDto
{
    [Required]
    [MaxLength(100)]
    public string Nombre { get; set; } = string.Empty;

    [Required]
    [MaxLength(100)]
    public string Apellido { get; set; } = string.Empty;

    [Required]
    [EmailAddress]
    [MaxLength(150)]
    public string Correo { get; set; } = string.Empty;

    // Formato esperado: "yyyy-MM-dd"
    [Required]
    public DateOnly? FechaNacimiento { get; set; }

    [Required]
    [MaxLength(150)]
    public string Universidad { get; set; } = string.Empty;

    [Range(1, 10)]
    public int Semestre { get; set; }

    public bool Activo { get; set; }
}
