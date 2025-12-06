package com.example.clubdeportivo

import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class NuevoUsuarioActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_usuario)

        // Helpers
        db = DBHelper(this)
        utils = AppUtils(this)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Inicializar views
        val etNombre     = findViewById<EditText>(R.id.etNombre)
        val etApellido   = findViewById<EditText>(R.id.etApellido)
        val etFecha      = findViewById<EditText>(R.id.etFecha)
        val etDNI        = findViewById<EditText>(R.id.etDNI)
        val etDireccion  = findViewById<EditText>(R.id.etDireccion)
        val etTelefono   = findViewById<EditText>(R.id.etTelefono)
        val etEmail      = findViewById<EditText>(R.id.etEmail)
        val btnRegistrar = findViewById<MaterialButton>(R.id.btnRegistrar)

        // Boton registrar onClick
        btnRegistrar.setOnClickListener {
            // Datos inputs
            val nombre    = etNombre.text.toString().trim()
            val apellido  = etApellido.text.toString().trim()
            val dni       = etDNI.text.toString().trim()
            val fecha  = etFecha.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val telefono  = etTelefono.text.toString().trim()
            val email     = etEmail.text.toString().trim()

            // Validaciones
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fecha.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                utils.toast("Todos los campos son obligatorios")
                return@setOnClickListener
            }
            if (!utils.esDniValido(dni)) {
                utils.toast("El DNI debe tener 8 o 9 números")
                etDNI.requestFocus()
                return@setOnClickListener
            }
            if (!utils.esTelefonoValido(telefono)) {
                utils.toast("Ingrese número de teléfono válido")
                etTelefono.requestFocus()
                return@setOnClickListener
            }
            if (!utils.esEmailValido(email)) {
                utils.toast("Ingrese un correo electrónico válido")
                etEmail.requestFocus()
                return@setOnClickListener
            }
            val fechaISO = utils.normalizarFecha(fecha)
            if (fechaISO == null) {
                utils.toast("Fecha inválida. Usá el formato DD/MM/AAAA")
                etFecha.requestFocus()
                return@setOnClickListener
            }

            // Chequeo DNI duplicado
            if (db.existeDni(dni)) {
                utils.toast("El DNI ingresado ya está registrado")
                etDNI.requestFocus()
                return@setOnClickListener
            }

            // Ventana confirmacion
            utils.confirmDialog(
                title = "Confirmar registro",
                message = "¿Confirmás registro nuevo usuario?"
            ) {
                try {
                    val rowId = db.insertarCliente(
                        dni = dni,
                        nombre = nombre,
                        apellido = apellido,
                        fechaNacISO = fechaISO,
                        direccion = direccion,
                        telefono = telefono,
                        email = email,
                        fechaInscripcionISO = utils.hoyIso()  // ahora lo vemos
                    )
                    utils.toast("Registro exitoso (ID $rowId)")
                    utils.goTo(InicioActivity::class.java, finishCurrent = true,"usuario" to usuario)
                    limpiarCampos(etNombre, etApellido, etFecha, etDNI, etDireccion, etTelefono, etEmail)
                } catch (e: SQLiteConstraintException) {
                    utils.toast("No se pudo registrar: ${e.message}")
                } catch (e: Exception) {
                    utils.toast("Error al registrar: ${e.localizedMessage}")
                }
            }
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_home)
    }
    private fun limpiarCampos(vararg edits: EditText) {
        edits.forEach { it.text.clear() }
    }
}