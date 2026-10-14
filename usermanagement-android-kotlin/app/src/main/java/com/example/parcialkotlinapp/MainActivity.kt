package com.example.parcialkotlinapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import java.util.Calendar

/**
 * Pantalla principal después del Login.
 *
 * Secciones de la actividad en este archivo:
 *  [4.5] Saludo personalizado según la hora. YA ESTABA IMPLEMENTADO en el
 *        proyecto base: no se modificó su lógica.
 *  [4.6] Cierre de sesión con ícono superior y AlertDialog de confirmación.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        // [4.5] SALUDO PERSONALIZADO (ya existía en el proyecto base, sin cambios):
        // el nombre llega desde el Login (usuario autenticado) y el saludo
        // depende de la hora actual del dispositivo.
        val tvSaludo =findViewById<TextView>(R.id.tvSaludo)

        // Nombre recibido desde LoginActivity
        val  nombreUsuario = intent.getStringExtra(
            "nombreUsuario"
        ) ?: "Usuario"

        //Hora Actual del dispositivo
        val horaActual =
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        //Determinar saludo
        var saludo = when(horaActual){

            in 5..11 ->
                "Buenos días"

            in 12..17 ->
                "Buenas tardes"

            else ->
                "Buenas noches"
        }

        //Mostrar saludo personalizado

        tvSaludo.text = "$saludo, $nombreUsuario"

        // Manejo de los bordes de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // =====================================
        // BOTÓN EJERCICIO PAR O IMPAR
        // =====================================

        val btnParImpar =
            findViewById<Button>(R.id.btnParImpar)

        btnParImpar.setOnClickListener {

            val intent = Intent(
                this,
                ParImparActivity::class.java
            )

            startActivity(intent)
        }

        // =====================================
        // [4.6] CERRAR SESIÓN (ícono en la esquina superior derecha)
        // =====================================

        val toolbar =
            findViewById<MaterialToolbar>(R.id.toolbarMain)

        toolbar.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_cerrar_sesion -> {
                    mostrarDialogoCerrarSesion()
                    true
                }

                else -> false
            }
        }

    }

    // =====================================
    // [4.6] ALERTDIALOG DE CONFIRMACIÓN
    // =====================================

    private fun mostrarDialogoCerrarSesion() {

        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Está seguro de que desea cerrar sesión?")
            // CANCELAR: solo cierra el cuadro y se queda en esta pantalla
            .setNegativeButton("CANCELAR") { dialogo, _ ->
                dialogo.dismiss()
            }
            // SALIR: finaliza la sesión y regresa al Login
            .setPositiveButton("SALIR") { _, _ ->
                cerrarSesion()
            }
            .show()
    }

    private fun cerrarSesion() {

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        // Borra todas las pantallas de la sesión anterior: con el botón
        // Atrás ya no se puede volver a la pantalla principal.
        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        )

        startActivity(intent)

        finish()
    }
}