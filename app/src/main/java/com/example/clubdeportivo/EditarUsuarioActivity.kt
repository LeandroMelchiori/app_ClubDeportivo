package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditarUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = DBHelper(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Recuperar datos del intent
        val id = intent.getIntExtra("id", -1)
        val dni = intent.getStringExtra("dni") ?: ""
        val esSocio = intent.getBooleanExtra("esSocio", false)




        // Inicializar views
        val etDni = findViewById<TextView>(R.id.etDni)
        val etNombre = findViewById<TextView>(R.id.etNombre)
        val etApellido = findViewById<TextView>(R.id.etApellido)
        val etTelefono = findViewById<TextView>(R.id.etTelefono)
        val etEmail = findViewById<TextView>(R.id.etEmail)
        val etDireccion = findViewById<TextView>(R.id.etDireccion)
        val etFechaNac = findViewById<TextView>(R.id.etFechaNac)

        // Llenar views
        val persona = db.obtenerPersonaPorDni(dni)
        etNombre.text = persona?.nombre
        etApellido.text = persona?.apellido
        etTelefono.text = persona?.telefono
        etEmail.text = persona?.email
        etDireccion.text = persona?.direccion
        etFechaNac.text = persona?.fecha_nac
        etDni.text = persona?.dni


        // Boton editar
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmar)
        btnConfirmar.setOnClickListener {
            if (esSocio == true) {
                db.actualizarSocioPorId(
                    idSocio     = id,
                    nombre      = etNombre.text.toString().trim(),
                    apellido    = etApellido.text.toString().trim(),
                    dni         = etDni.text.toString().trim(),
                    fechaNac    = etFechaNac.text.toString().trim(),
                    telefono    = etTelefono.text.toString().trim(),
                    direccion   = etDireccion.text.toString().trim(),
                    email       = etEmail.text.toString().trim()
                )
                Toast.makeText(this, "Socio actualizado con exito", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                db.actualizarNoSocioPorId(
                    idNoSocio   = id,
                    nombre      = etNombre.text.toString().trim(),
                    apellido    = etApellido.text.toString().trim(),
                    dni         = etDni.text.toString().trim(),
                    fechaNac    = etFechaNac.text.toString().trim(),
                    telefono    = etTelefono.text.toString().trim(),
                    direccion   = etDireccion.text.toString().trim(),
                    email       = etEmail.text.toString().trim(),
                )
                Toast.makeText(this, "No socio actualizado con exito", Toast.LENGTH_SHORT).show()
                finish()
            }
        }


        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pagos -> {
                    val intent = Intent(this, PagosActivity::class.java)
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