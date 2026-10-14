package com.example.parcialkotlinapp.models

// [4.4] Usuario devuelto por la API (sincronizado con UsuarioDto del backend):
// ya no trae edad; ahora trae fechaNacimiento, universidad y semestre.
data class Usuario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val fechaNacimiento: String,
    val universidad: String,
    val semestre: Int,
    val activo: Boolean,
    val fechaCreacion: String
)
