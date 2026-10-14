package com.example.parcialkotlinapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parcialkotlinapp.api.ApiClient
import com.example.parcialkotlinapp.api.ApiErrorParser
import com.example.parcialkotlinapp.models.CrearUsuarioRequest
import com.example.parcialkotlinapp.models.Usuario
import com.example.parcialkotlinapp.utils.EdadUtils
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

/**
 * Pantalla de registro de usuario.
 *
 * Secciones de la actividad implementadas en este archivo:
 *  [4.1] Fecha de nacimiento con DatePicker, edad calculada, Universidad y
 *        Semestre (1 a 10). Se eliminaron Rol, Edad manual y el botón
 *        "VOLVER AL LOGIN".
 *  [4.2] AlertDialog "Registro exitoso" que regresa al Login.
 *  [4.3] Mensajes del backend (ej. correo duplicado) en lugar de un error genérico.
 *  [4.8] Flecha de navegación superior.
 */
class RegistroActivity : AppCompatActivity() {

    // Fecha elegida en el DatePicker: (año, mes en base 1, día).
    // null mientras el usuario no ha seleccionado ninguna.
    private var fechaSeleccionada: Triple<Int, Int, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_registro)

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
        // [4.1 / 4.8] FLECHA SUPERIOR: REGRESA A LA PANTALLA ANTERIOR (LOGIN)
        // =====================================

        val toolbar =
            findViewById<MaterialToolbar>(R.id.toolbarRegistro)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // =====================================
        // COMPONENTES
        // =====================================

        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etPasswordRegistro = findViewById<EditText>(R.id.etPasswordRegistro)

        val etFechaNacimiento =
            findViewById<EditText>(R.id.etFechaNacimiento)

        val tvEdadCalculada =
            findViewById<TextView>(R.id.tvEdadCalculada)

        val etUniversidad = findViewById<EditText>(R.id.etUniversidad)

        val spSemestre = findViewById<Spinner>(R.id.spSemestre)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        val tvMensaje =
            findViewById<TextView>(R.id.tvMensajeRegistro)

        // Color original del mensaje, para restaurarlo después de un error
        val colorMensajeNormal = tvMensaje.currentTextColor

        val colorMensajeError =
            ContextCompat.getColor(this, android.R.color.holo_red_dark)

        // =====================================
        // [4.1] SEMESTRE: SOLO VALORES VÁLIDOS (1 A 10)
        // =====================================

        val semestres = arrayOf("Seleccione semestre") +
                (1..10).map { it.toString() }

        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            semestres
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spSemestre.adapter = adaptador

        // =====================================
        // [4.1] FECHA DE NACIMIENTO (DatePicker)
        // =====================================

        etFechaNacimiento.setOnClickListener {
            mostrarDatePicker(etFechaNacimiento, tvEdadCalculada)
        }

        // =====================================
        // BOTÓN REGISTRAR
        // =====================================

        btnRegistrar.setOnClickListener {

            val nombre =
                etNombre.text.toString().trim()

            val apellido =
                etApellido.text.toString().trim()

            val correo =
                etCorreo.text.toString().trim()

            val password = etPasswordRegistro.text.toString().trim()

            val universidad =
                etUniversidad.text.toString().trim()

            // VALIDACIONES
            if (nombre.isBlank()) {
                etNombre.error = "Ingrese el nombre"
                return@setOnClickListener
            }

            if (apellido.isBlank()) {
                etApellido.error = "Ingrese el apellido"
                return@setOnClickListener
            }

            if (correo.isBlank()) {
                etCorreo.error = "Ingrese el correo"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS
                    .matcher(correo)
                    .matches()) {

                etCorreo.error = "Correo no válido"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                etPasswordRegistro.error = "Ingrese la contraseña"
                return@setOnClickListener
            }

            // El backend exige mínimo 6 caracteres
            if (password.length < 6) {
                etPasswordRegistro.error =
                    "La contraseña debe tener mínimo 6 caracteres"
                return@setOnClickListener
            }

            val fecha = fechaSeleccionada

            if (fecha == null) {
                etFechaNacimiento.error =
                    "Seleccione la fecha de nacimiento"

                Toast.makeText(
                    this,
                    "Seleccione la fecha de nacimiento",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (universidad.isBlank()) {
                etUniversidad.error = "Ingrese la universidad"
                return@setOnClickListener
            }

            // Posición 0 es el texto "Seleccione semestre".
            // Las posiciones 1..10 coinciden con el semestre elegido.
            if (spSemestre.selectedItemPosition == 0) {

                Toast.makeText(
                    this,
                    "Seleccione un semestre entre 1 y 10",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val semestre = spSemestre.selectedItemPosition

            // OBJETO QUE ENVIAREMOS AL BACKEND
            // [4.1] La edad no se envía: se calcula a partir de la fecha.
            // [4.1] Ya no se envía rolId (el Rol se eliminó del registro).
            val nuevoUsuario = CrearUsuarioRequest(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                password = password,
                fechaNacimiento = EdadUtils.formatoApi(
                    fecha.first,
                    fecha.second,
                    fecha.third
                ),
                universidad = universidad,
                semestre = semestre
            )

            tvMensaje.setTextColor(colorMensajeNormal)
            tvMensaje.text = "Registrando usuario..."

            // Evitar registros duplicados por varios clics
            btnRegistrar.isEnabled = false

            // LLAMADA AL BACKEND
            ApiClient.usuarioApi
                .crearUsuario(nuevoUsuario)
                .enqueue(object : Callback<Usuario> {

                    override fun onResponse(
                        call: Call<Usuario>,
                        response: Response<Usuario>
                    ) {

                        btnRegistrar.isEnabled = true

                        // =====================================
                        // [4.2] REGISTRO EXITOSO
                        // =====================================
                        if (response.isSuccessful) {

                            tvMensaje.text = ""

                            mostrarDialogoRegistroExitoso()

                        }
                        // =====================================
                        // [4.3] ERROR CONTROLADO DEL BACKEND
                        // Ej: HTTP 400 { "mensaje": "Ya existe un usuario con ese correo." }
                        // =====================================
                        else {

                            val mensaje = ApiErrorParser.mensaje(
                                response,
                                "Error del servidor: ${response.code()}"
                            )

                            tvMensaje.setTextColor(colorMensajeError)
                            tvMensaje.text = mensaje

                            // Si el problema es el correo, marcar ese campo
                            if (response.code() == 400 &&
                                mensaje.contains("correo", ignoreCase = true)) {

                                etCorreo.error = mensaje
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<Usuario>,
                        t: Throwable
                    ) {

                        btnRegistrar.isEnabled = true

                        tvMensaje.setTextColor(colorMensajeError)
                        tvMensaje.text =
                            "Error de conexión: ${t.message}"
                    }
                })
        }
    }

    // =====================================
    // [4.1] DATEPICKER PARA LA FECHA DE NACIMIENTO
    // =====================================

    private fun mostrarDatePicker(
        etFecha: EditText,
        tvEdad: TextView
    ) {

        val hoy = Calendar.getInstance()

        val seleccion = fechaSeleccionada

        // Si ya eligió una fecha, el selector se abre en ella.
        // Si no, se abre unos 18 años atrás, que es lo más común.
        val anioInicial =
            seleccion?.first ?: (hoy.get(Calendar.YEAR) - 18)

        // El DatePicker maneja los meses en base 0; nosotros guardamos base 1.
        val mesInicial =
            (seleccion?.second ?: (hoy.get(Calendar.MONTH) + 1)) - 1

        val diaInicial =
            seleccion?.third ?: hoy.get(Calendar.DAY_OF_MONTH)

        val dialogo = DatePickerDialog(
            this,
            { _, anio, mesBase0, dia ->

                val mes = mesBase0 + 1

                fechaSeleccionada = Triple(anio, mes, dia)

                etFecha.setText(
                    EdadUtils.formatoVisible(anio, mes, dia)
                )

                etFecha.error = null

                // [4.1] La edad se calcula con día, mes y año (ver EdadUtils)
                val edad = EdadUtils.calcularEdadHoy(anio, mes, dia)

                tvEdad.text =
                    if (edad == 1) "Edad: 1 año"
                    else "Edad: $edad años"
            },
            anioInicial,
            mesInicial,
            diaInicial
        )

        // No se puede elegir una fecha futura
        dialogo.datePicker.maxDate = hoy.timeInMillis

        dialogo.show()
    }

    // =====================================
    // [4.2] ALERTDIALOG DE REGISTRO EXITOSO
    // =====================================

    private fun mostrarDialogoRegistroExitoso() {

        AlertDialog.Builder(this)
            .setTitle("Registro exitoso")
            .setMessage("El usuario fue creado exitosamente.")
            .setCancelable(false)
            .setPositiveButton("ACEPTAR") { dialogo, _ ->

                dialogo.dismiss()

                // Regresa al Login para iniciar sesión con la cuenta creada
                finish()
            }
            .show()
    }
}
