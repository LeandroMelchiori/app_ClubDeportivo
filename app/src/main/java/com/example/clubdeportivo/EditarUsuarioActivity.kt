package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditarUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = DBHelper(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Fecha encabezado
        val tvFecha = findViewById<TextView>(R.id.tvFechaHoy)
        val formato = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "AR"))
        val fechaHoy = formato.format(Date())
        tvFecha.text = fechaHoy.replaceFirstChar { it.uppercase() }

        // Recuperar datos del intent
        val id = intent.getIntExtra("id", -1)
        val dni = intent.getStringExtra("dni") ?: ""

        // Inicializar views
        val etDni = findViewById<TextView>(R.id.etDni)
        val etNombre = findViewById<TextView>(R.id.etNombre)
        val etApellido = findViewById<TextView>(R.id.etApellido)
        val etTelefono = findViewById<TextView>(R.id.etTelefono)
        val etEmail = findViewById<TextView>(R.id.etEmail)
        val etDireccion = findViewById<TextView>(R.id.etDireccion)
        val etFechaNac = findViewById<TextView>(R.id.etFechaNac)

        // Llenar views
        val persona = db.buscarPersonaPorDni(dni)
        etNombre.text = persona?.nombre
        etApellido.text = persona?.apellido
        etTelefono.text = persona?.telefono
        etEmail.text = persona?.email
        etDireccion.text = persona?.direccion
        etFechaNac.text = persona?.fecha_nac
        etDni.text = persona?.dni

        // Campo dni deshabilitado
        etDni.isEnabled = false

        // Boton editar
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        btnConfirmar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val fechaNac = etFechaNac.text.toString().trim()
            val telefono = etTelefono.text.toString().trim()
            val direccion = etDireccion.text.toString().trim()
            val email = etEmail.text.toString().trim()

            // Validar campos vacios
            if (nombre.isEmpty() || apellido.isEmpty() ||fechaNac.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            //  Validar DNI
            if (!dni.matches(Regex("^\\d{8,9}\$"))) {
                Toast.makeText(this, "El DNI debe tener 8 o 9 números", Toast.LENGTH_LONG).show()
                etDni.requestFocus()
                return@setOnClickListener
            }
            // Validar teléfono
            if (!telefono.matches(Regex("^\\d{9,12}\$"))) {
                Toast.makeText(this, "Ingrese numerode telefono valido", Toast.LENGTH_LONG).show()
                etTelefono.requestFocus()
                return@setOnClickListener
            }
            // Validar email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_LONG).show()
                etEmail.requestFocus()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar edicion")
                .setMessage("¿Confirmás editar al cliente con DNI: $dni?")
                .setPositiveButton("Sí") { _, _ ->
                    try {
                        db.actualizarClientePorId(
                            id          = id,
                            nombre      = etNombre.text.toString().trim(),
                            apellido    = etApellido.text.toString().trim(),
                            dni         = etDni.text.toString().trim(),
                            fechaNac    = etFechaNac.text.toString().trim(),
                            telefono    = etTelefono.text.toString().trim(),
                            direccion   = etDireccion.text.toString().trim(),
                            email       = etEmail.text.toString().trim()
                        )
                        val intent = Intent(this, VerMasActivity::class.java)
                        intent.putExtra("dni", dni)
                        startActivity(intent)
                        Toast.makeText(this, "Cliente actualizado con exito", Toast.LENGTH_SHORT).show()
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(this, e.message ?: "Error al actualizar usuario", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pagos -> {
                    val intent = Intent(this, ResumenMensualActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }

                R.id.nav_activity -> {
                    val intent = Intent(this, ActividadesActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }

                R.id.nav_settings -> {
                    val intent = Intent(this, ConfiguracionActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }

                R.id.nav_listas -> {
                    val intent = Intent(this, ListadosActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }

                R.id.nav_home -> {
                    val intent = Intent(this, InicioActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }
                else -> true
            }
        }
    }
}