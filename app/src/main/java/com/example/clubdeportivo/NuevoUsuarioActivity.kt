package com.example.clubdeportivo

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NuevoUsuarioActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_usuario)

        db = DBHelper(this).writableDatabase
        utils = AppUtils(this)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Inputs
        val etNombre     = findViewById<EditText>(R.id.etNombre)
        val etApellido   = findViewById<EditText>(R.id.etApellido)
        val etFecha      = findViewById<EditText>(R.id.etFecha)
        val etDNI        = findViewById<EditText>(R.id.etDNI)
        val etDireccion  = findViewById<EditText>(R.id.etDireccion)
        val etTelefono   = findViewById<EditText>(R.id.etTelefono)
        val etEmail      = findViewById<EditText>(R.id.etEmail)

        // Boton registro
        findViewById<MaterialButton>(R.id.btnRegistrar)
            .setOnClickListener {
                val nombre    = etNombre.text.toString().trim()
                val apellido  = etApellido.text.toString().trim()
                val dni       = etDNI.text.toString().trim()
                val fecha  = etFecha.text.toString().trim()
                val direccion = etDireccion.text.toString().trim()
                val telefono  = etTelefono.text.toString().trim()
                val email     = etEmail.text.toString().trim()

                // Validaciones campos vacios
                if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fecha.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                    utils.toast("Todos los campos son obligatorios")
                    return@setOnClickListener
                }

                //  Validar DNI
                if (!dni.matches(Regex("^\\d{8,9}\$"))) {
                    utils.toast("El DNI debe tener 8 o 9 números")
                    etDNI.requestFocus()
                    return@setOnClickListener
                }

                // Validar teléfono
                if (!telefono.matches(Regex("^\\d{9,12}\$"))) {
                    utils.toast("Ingrese numerode telefono valido")
                    etTelefono.requestFocus()
                    return@setOnClickListener
                }

                // Validar email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    utils.toast("Ingrese un correo electrónico válido")
                    etEmail.requestFocus()
                    return@setOnClickListener
                }

                // Normaliza la fecha de nacimiento
                val fechaISO = normalizarFecha(fecha)
                // Fecha hoy
                val hoyISO = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
                // Tabla
                val tabla = "clientes"

                // Valores a insertar
                val values = ContentValues().apply {
                    put("dni", dni.trim())
                    put("nombre", nombre.trim())
                    put("apellido", apellido.trim())
                    fechaISO?.let { put("fecha_nac", it) }
                    put("direccion", direccion)
                    put("telefono", telefono.trim())
                    put("email", email.trim())
                    put("fecha_inscripcion", hoyISO)
                    put("activo", 1)
                    put("ficha_medica", 1)
                }

                // Chequeo DNI duplicado
                if (existeDni(db, tabla, dni)) {
                    utils.toast("El DNI ya está registrado")
                    return@setOnClickListener
                }

                // Ventana confirmacion
                utils.confirmDialog(
                    title = "Confirmar registro",
                    message = "¿Confirmás registro nuevo usuario?"
                ) {
                    try {
                        val rowId = db.insertOrThrow(tabla, null, values)
                        utils.toast("Registro exitoso (ID $rowId)")
                        utils.goTo(InicioActivity::class.java, usuario, finishCurrent = true)

                        etNombre.text.clear()
                        etApellido.text.clear()
                        etFecha.text.clear()
                        etDNI.text.clear()
                        etDireccion.text.clear()
                        etTelefono.text.clear()
                        etEmail.text.clear()
                    } catch (e: android.database.sqlite.SQLiteConstraintException) {
                        Log.e("DB", "Constraint al insertar: ${e.message}")
                        utils.toast("No se pudo registrar: ${e.message}")
                    } catch (e: Exception) {
                        Log.e("DB", "Error al insertar", e)
                        utils.toast("Error al registrar: ${e.localizedMessage}")
                    }
                }
            }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home
        utils.setupBottomNav(bottom, usuario, R.id.nav_home)
    }
    // Metodo para normalizar la fecha
    private fun normalizarFecha(input: String): String? {
        if (input.isBlank()) return null
        return try {
            val inFmt  = SimpleDateFormat("dd/MM/yyyy", Locale("es", "AR"))
            val outFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            outFmt.format(inFmt.parse(input)!!)
        } catch (_: Exception) {
            null
        }
    }

    // Metodo para chequear si el DNI ya está registrado
    private fun existeDni(db: SQLiteDatabase, tabla: String, dni: String): Boolean {
        db.rawQuery("SELECT COUNT(1) FROM $tabla WHERE dni = ?", arrayOf(dni)).use { c ->
            return c.moveToFirst() && c.getInt(0) > 0
        }
    }

}