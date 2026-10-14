package com.example.parcialkotlinapp.api

import com.google.gson.JsonParser
import retrofit2.Response

/**
 * [4.3] Interpreta el cuerpo de las respuestas de error de la API para mostrar
 * el mensaje real que envió el backend en lugar de un error genérico.
 *
 * Formatos que entiende:
 *  1) Validaciones controladas del servicio:
 *       HTTP 400  { "mensaje": "Ya existe un usuario con ese correo." }
 *  2) Validaciones automáticas de ASP.NET (DataAnnotations):
 *       HTTP 400  { "title": "...", "errors": { "Campo": ["..."] } }
 */
object ApiErrorParser {

    fun mensaje(response: Response<*>, porDefecto: String): String {

        val cuerpo = try {
            response.errorBody()?.string()
        } catch (e: Exception) {
            null
        }

        if (cuerpo.isNullOrBlank()) {
            return porDefecto
        }

        return try {
            val json = JsonParser.parseString(cuerpo)

            if (!json.isJsonObject) {
                return porDefecto
            }

            val objeto = json.asJsonObject

            // 1) Mensaje controlado del backend
            val mensaje = objeto.get("mensaje")

            if (mensaje != null && mensaje.isJsonPrimitive) {
                val texto = mensaje.asString

                if (texto.isNotBlank()) {
                    return texto
                }
            }

            // 2) Errores de validación del modelo
            val errores = objeto.get("errors")

            if (errores != null && errores.isJsonObject) {
                return "Los datos enviados no son válidos. Revise el formulario."
            }

            porDefecto

        } catch (e: Exception) {
            porDefecto
        }
    }
}
