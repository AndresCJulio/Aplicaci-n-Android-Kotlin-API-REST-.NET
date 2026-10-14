using UsuariosApi.DTOs;
using UsuariosApi.Entities;
using UsuariosApi.Interfaces;

namespace UsuariosApi.Services;

public class UsuarioService : IUsuarioService
{
    private readonly IUsuarioRepository _repo;

    public UsuarioService(IUsuarioRepository repo)
    {
        _repo = repo;
    }

    // ==========================================
    // OBTENER TODOS
    // ==========================================
    public async Task<List<UsuarioDto>> ObtenerTodosAsync()
    {
        var usuarios = await _repo.ObtenerTodosAsync();

        return usuarios
            .Select(Map)
            .ToList();
    }

    // ==========================================
    // OBTENER POR ID
    // ==========================================
    public async Task<UsuarioDto?> ObtenerPorIdAsync(int id)
    {
        var usuario = await _repo.ObtenerPorIdAsync(id);

        return usuario is null
            ? null
            : Map(usuario);
    }

    // ==========================================
    // CREAR USUARIO
    // ==========================================
    public async Task<UsuarioDto> CrearAsync(CrearUsuarioDto d)
    {
        var correo = d.Correo
            .Trim()
            .ToLowerInvariant();

        // Validar que el correo no esté registrado
        if (await _repo.ObtenerPorCorreoAsync(correo) is not null)
        {
            throw new InvalidOperationException(
                "Ya existe un usuario con ese correo."
            );
        }

        // [4.4] Validar fecha de nacimiento (no futura, no absurda)
        var fechaNacimiento =
            ValidarFechaNacimiento(d.FechaNacimiento);

        // Convertir contraseña en HASH
        var passwordHash =
            BCrypt.Net.BCrypt.HashPassword(d.Password);

        // [4.4] Ya no se asigna RolId ni Edad; se guardan FechaNacimiento,
        // Universidad y Semestre. Se mantiene el hash de la contraseña (BCrypt)
        // y la validación de correo duplicado de arriba.
        var usuario = new Usuario
        {
            Nombre = d.Nombre.Trim(),
            Apellido = d.Apellido.Trim(),
            Correo = correo,
            PasswordHash = passwordHash,
            FechaNacimiento = fechaNacimiento,
            Universidad = d.Universidad.Trim(),
            Semestre = d.Semestre
        };

        await _repo.CrearAsync(usuario);

        return Map(usuario);
    }

    // ==========================================
    // LOGIN
    // ==========================================
    public async Task<UsuarioDto?> LoginAsync(LoginDto dto)
    {
        var correo = dto.Correo
            .Trim()
            .ToLowerInvariant();

        // Buscar usuario por correo
        var usuario =
            await _repo.ObtenerPorCorreoAsync(correo);

        // Usuario no encontrado
        if (usuario is null)
        {
            return null;
        }

        // Validar que esté activo
        if (!usuario.Activo)
        {
            return null;
        }

        // Validar que tenga contraseña registrada
        if (string.IsNullOrWhiteSpace(usuario.PasswordHash))
        {
            return null;
        }

        // Comparar la contraseña ingresada
        // con el HASH almacenado en la BD
        var passwordCorrecto =
            BCrypt.Net.BCrypt.Verify(
                dto.Password,
                usuario.PasswordHash
            );

        // Contraseña incorrecta
        if (!passwordCorrecto)
        {
            return null;
        }

        // LOGIN CORRECTO
        return Map(usuario);
    }

    // ==========================================
    // ACTUALIZAR
    // ==========================================
    public async Task<bool> ActualizarAsync(
        int id,
        ActualizarUsuarioDto d)
    {
        var usuario =
            await _repo.ObtenerPorIdAsync(id);

        if (usuario is null)
        {
            return false;
        }

        var correo = d.Correo
            .Trim()
            .ToLowerInvariant();

        var otro =
            await _repo.ObtenerPorCorreoAsync(correo);

        if (otro is not null &&
            otro.Id != id)
        {
            throw new InvalidOperationException(
                "El correo pertenece a otro usuario."
            );
        }

        // [4.4] Actualizar con FechaNacimiento, Universidad y Semestre (sin Edad)
        var fechaNacimiento =
            ValidarFechaNacimiento(d.FechaNacimiento);

        usuario.Nombre = d.Nombre.Trim();
        usuario.Apellido = d.Apellido.Trim();
        usuario.Correo = correo;
        usuario.FechaNacimiento = fechaNacimiento;
        usuario.Universidad = d.Universidad.Trim();
        usuario.Semestre = d.Semestre;
        usuario.Activo = d.Activo;

        await _repo.ActualizarAsync(usuario);

        return true;
    }

    // ==========================================
    // ELIMINAR
    // ==========================================
    public async Task<bool> EliminarAsync(int id)
    {
        var usuario =
            await _repo.ObtenerPorIdAsync(id);

        if (usuario is null)
        {
            return false;
        }

        await _repo.EliminarAsync(usuario);

        return true;
    }

    // ==========================================
    // [4.4] VALIDAR FECHA DE NACIMIENTO
    // ==========================================
    // Las reglas de negocio se reportan como
    // InvalidOperationException para que el controlador
    // las convierta en HTTP 400 con { "mensaje": ... }
    private static DateOnly ValidarFechaNacimiento(
        DateOnly? fecha)
    {
        if (fecha is null)
        {
            throw new InvalidOperationException(
                "La fecha de nacimiento es obligatoria."
            );
        }

        var hoy = DateOnly.FromDateTime(DateTime.Today);

        if (fecha.Value > hoy)
        {
            throw new InvalidOperationException(
                "La fecha de nacimiento no puede ser futura."
            );
        }

        if (fecha.Value < hoy.AddYears(-120))
        {
            throw new InvalidOperationException(
                "La fecha de nacimiento no es válida."
            );
        }

        return fecha.Value;
    }

    // ==========================================
    // MAPEO ENTITY -> DTO  [4.4] (ahora con FechaNacimiento, Universidad y Semestre;
    // sin Edad y sin exponer PasswordHash)
    // ==========================================
    private static UsuarioDto Map(Usuario u)
    {
        return new UsuarioDto
        {
            Id = u.Id,
            Nombre = u.Nombre,
            Apellido = u.Apellido,
            Correo = u.Correo,
            FechaNacimiento = u.FechaNacimiento,
            Universidad = u.Universidad,
            Semestre = u.Semestre,
            Activo = u.Activo,
            FechaCreacion = u.FechaCreacion
        };
    }
}