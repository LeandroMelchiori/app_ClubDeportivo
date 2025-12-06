package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditarUsuarioActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        // Helpers
        db = DBHelper(this)
        utils = AppUtils(this)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Fecha encabezado
        val tvFecha = findViewById<TextView>(R.id.tvFechaHoy)
        tvFecha.text = utils.fechaActualFormato()

        // Recuperar dni del intent y buscamos en BD
        val dni = intent.getStringExtra("dni") ?: run {
            utils.toast("No se recibió el DNI del usuario")
            finish()
            return
        }
        val persona = db.buscarPersonaPorDni(dni)

        // Inicializar views
        val etDni = findViewById<TextView>(R.id.etDni)
        val etNombre = findViewById<TextView>(R.id.etNombre)
        val etApellido = findViewById<TextView>(R.id.etApellido)
        val etTelefono = findViewById<TextView>(R.id.etTelefono)
        val etEmail = findViewById<TextView>(R.id.etEmail)
        val etDireccion = findViewById<TextView>(R.id.etDireccion)
        val etFecha = findViewById<TextView>(R.id.etFechaNac)

        // Llenar campos con datos de la persona
        etNombre.text = persona?.nombre
        etApellido.text = persona?.apellido
        etTelefono.text = persona?.telefono
        etEmail.text = persona?.email
        etDireccion.text = persona?.direccion
        etFecha.text = persona?.fecha_nac
        etDni.text = persona?.dni
        // Campo dni deshabilitado
        etDni.isEnabled = false

        // Boton editar
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        btnConfirmar.setOnClickListener {
            // Capturar datos actualizados
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val fecha = etFecha.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val email = etEmail.text.toString().trim()

            // Validaciones
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fecha.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                utils.toast("Todos los campos son obligatorios")
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

            // Dialogo de confirmacion
            utils.confirmDialog(
                "Confirmar edición",
                "¿Confirmás editar al cliente con DNI: $dni?"
            ) {
                try {
                    db.actualizarClientePorId(
                        id = persona!!.id,
                        nombre = nombre,
                        apellido = apellido,
                        dni = dni,
                        fechaNac = fecha,
                        telefono = telefono,
                        direccion = direccion,
                        email = email
                    )
                    utils.goTo(VerMasActivity::class.java, true, "dni" to dni)
                    utils.toast("Cliente actualizado con exito")
                } catch (e: IllegalArgumentException) {
                    utils.toast("No se pudo actualizar: ${e.message}")
                } catch (e: Exception) {
                    utils.toast("Error al actualizar: ${e.localizedMessage}")
                }
            }
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_listas)
    }
}