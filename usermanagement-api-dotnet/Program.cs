using Microsoft.EntityFrameworkCore;
using UsuariosApi.Data;
using UsuariosApi.Interfaces;
using UsuariosApi.Repositories;
using UsuariosApi.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();

var connectionString =
    builder.Configuration.GetConnectionString("MySql");

builder.Services.AddDbContext<ApplicationDbContext>(options =>
    options.UseMySql(
        connectionString,
        ServerVersion.AutoDetect(connectionString)
    ));

builder.Services.AddScoped<IUsuarioRepository, UsuarioRepository>();
builder.Services.AddScoped<IUsuarioService, UsuarioService>();

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// Para la práctica local con el emulador Android
// evitamos redireccionar HTTP hacia HTTPS.
// app.UseHttpsRedirection();

app.MapControllers();

app.Run();