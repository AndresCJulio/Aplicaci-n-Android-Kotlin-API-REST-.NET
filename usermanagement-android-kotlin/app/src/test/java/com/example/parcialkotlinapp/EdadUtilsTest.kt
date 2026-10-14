package com.example.parcialkotlinapp

import com.example.parcialkotlinapp.utils.EdadUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class EdadUtilsTest {

    // Fecha "de hoy" fija para las pruebas: 12/10/2026
    private fun edad(anio: Int, mes: Int, dia: Int) =
        EdadUtils.calcularEdad(anio, mes, dia, 2026, 10, 12)

    @Test
    fun cumpleanosYaPasadoEsteAnio() {
        assertEquals(26, edad(2000, 3, 10))
    }

    @Test
    fun cumpleanosEnMesPosteriorTodaviaNoLlega() {
        assertEquals(25, edad(2000, 12, 25))
    }

    @Test
    fun mismoMesPeroDiaPosteriorTodaviaNoLlega() {
        assertEquals(25, edad(2000, 10, 20))
    }

    @Test
    fun hoyEsElCumpleanos() {
        assertEquals(26, edad(2000, 10, 12))
    }

    @Test
    fun ayerFueElCumpleanos() {
        assertEquals(26, edad(2000, 10, 11))
    }

    @Test
    fun cumpleanosEsMananaAunTieneUnAnioMenos() {
        assertEquals(25, edad(2000, 10, 13))
    }

    @Test
    fun nacioHoyTiene0Anios() {
        assertEquals(0, edad(2026, 10, 12))
    }

    @Test
    fun noBastaConRestarLosAnios() {
        // Restar solo el año daría 26, pero aún no cumple años
        assertEquals(
            25,
            EdadUtils.calcularEdad(2000, 12, 31, 2026, 1, 1)
        )
    }

    @Test
    fun nacidoUn29DeFebrero() {
        assertEquals(
            21,
            EdadUtils.calcularEdad(2004, 2, 29, 2026, 2, 28)
        )
        assertEquals(
            22,
            EdadUtils.calcularEdad(2004, 2, 29, 2026, 3, 1)
        )
    }

    @Test
    fun formatosDeFecha() {
        assertEquals("2000-05-09", EdadUtils.formatoApi(2000, 5, 9))
        assertEquals("09/05/2000", EdadUtils.formatoVisible(2000, 5, 9))
    }
}
