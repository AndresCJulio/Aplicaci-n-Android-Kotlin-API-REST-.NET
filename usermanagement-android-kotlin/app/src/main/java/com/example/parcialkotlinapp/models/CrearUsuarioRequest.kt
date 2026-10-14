package com.example.parcialkotlinapp.models

// [4.1 / 4.4] Datos de registro enviados a la API.
// Se eliminaron edad y rolId; se agregaron fechaNacimiento, universidad y semestre.
data class CrearUsuarioRequest(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val password: String,
    // Formato "yyyy-MM-dd", el mismo que espera el backend (DateOnly)
    val fechaNacimiento: String,
    val universidad: String,
    val semestre: Int
)
