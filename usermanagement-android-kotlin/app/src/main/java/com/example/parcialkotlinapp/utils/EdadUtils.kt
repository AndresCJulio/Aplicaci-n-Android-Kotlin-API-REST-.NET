package com.example.parcialkotlinapp.utils

import java.util.Calendar
import java.util.Locale

/**
 * [4.1] Cálculo de la edad a partir de la fecha de nacimiento
 * (considera día, mes y año) y formatos de fecha para la API y la pantalla.
 */
object EdadUtils {

    /**
     * Calcula la edad en años cumplidos considerando año, mes y día.
     * No basta con restar los años: si el cumpleaños de este año todavía
     * no ha llegado, la persona aún tiene un año menos.
     *
     * Los meses se reciben en base 1 (enero = 1 ... diciembre = 12).
     */
    fun calcularEdad(
        anioNacimiento: Int,
        mesNacimiento: Int,
        diaNacimiento: Int,
        anioActual: Int,
        mesActual: Int,
        diaActual: Int
    ): Int {

        var edad = anioActual - anioNacimiento

        val cumpleanosPendiente =
            mesActual < mesNacimiento ||
                    (mesActual == mesNacimiento && diaActual < diaNacimiento)

        if (cumpleanosPendiente) {
            edad--
        }

        return edad
    }

    /**
     * Calcula la edad respecto a la fecha actual del dispositivo.
     * Recibe el mes en base 1.
     */
    fun calcularEdadHoy(
        anioNacimiento: Int,
        mesNacimiento: Int,
        diaNacimiento: Int
    ): Int {

        val hoy = Calendar.getInstance()

        return calcularEdad(
            anioNacimiento,
            mesNacimiento,
            diaNacimiento,
            hoy.get(Calendar.YEAR),
            hoy.get(Calendar.MONTH) + 1,
            hoy.get(Calendar.DAY_OF_MONTH)
        )
    }

    /**
     * Fecha en formato "yyyy-MM-dd" (el que espera la API).
     * Recibe el mes en base 1.
     */
    fun formatoApi(anio: Int, mes: Int, dia: Int): String =
        String.format(Locale.US, "%04d-%02d-%02d", anio, mes, dia)

    /**
     * Fecha en formato "dd/MM/yyyy" para mostrar al usuario.
     * Recibe el mes en base 1.
     */
    fun formatoVisible(anio: Int, mes: Int, dia: Int): String =
        String.format(Locale.US, "%02d/%02d/%04d", dia, mes, anio)
}
