package com.example.parcialkotlinapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

/**
 * Ejercicio Par o Impar.
 *
 *  [4.7] Se mantiene la validación; se eliminó el botón "VOLVER APPS" y se
 *        agregó la flecha superior que regresa a la pantalla principal.
 *  [4.8] Navegación uniforme: flecha superior en lugar de botones inferiores.
 */
class ParImparActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_par_impar)

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
        // [4.7 / 4.8] FLECHA SUPERIOR: REGRESA A LA PANTALLA PRINCIPAL
        // =====================================

        val toolbar =
            findViewById<MaterialToolbar>(R.id.toolbarParImpar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Conectamos Kotlin con los componentes del XML
        val etNumero = findViewById<EditText>(R.id.etNumero)
        val btnValidar = findViewById<Button>(R.id.btnValidar)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)


        btnValidar.setOnClickListener {

            val texto = etNumero.text.toString()

            // Validar que el campo no esté vacío
            if (texto.isEmpty()) {
                etNumero.error = "Ingrese un número"
                return@setOnClickListener
            }

            // Conversión segura
            val numero = texto.toIntOrNull()

            if (numero == null) {
                etNumero.error = "Ingrese un número entero válido"
                return@setOnClickListener
            }

            // Validación par o impar
            if (numero % 2 == 0) {
                tvResultado.text = "$numero es PAR"
            } else {
                tvResultado.text = "$numero es IMPAR"
            }
        }
    }
}
