package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubdeportivo.DBHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class VerMasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mas)

        // DB Helper
        val db = DBHelper(this)

        // Recupera el nombre de usuario del intent y lo muestra
        val admin = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $admin"

        // Recupera dni del intent y busca a la persona en la BBDD
        val dniUsuario = intent.getStringExtra("dni") ?: "dni"
        val usuario = db.obtenerPersonaPorDni(dniUsuario)

        //Inicializar vistas
        val tvNombreCompleto = findViewById<TextView>(R.id.tvNombreUsuario)
        val tvDNI = findViewById<TextView>(R.id.tvDNI)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvDireccion = findViewById<TextView>(R.id.tvDireccion)
        val tvTelefono = findViewById<TextView>(R.id.tvTelefono)
        val tvFechaNacimiento = findViewById<TextView>(R.id.tvFechaNacimiento)

        // Reemplaza datos en las view
        tvNombreCompleto.text = "${usuario?.nombre}, ${usuario?.apellido} "
        tvDNI.text = "DNI: ${usuario?.dni}"
        tvTelefono.text = "Telefono: ${usuario?.telefono}"
        tvDireccion.text = "Domicilio: ${usuario?.direccion}"
        tvFechaNacimiento.text = "Fecha de nacimiento: ${usuario?.fecha_nac}"
        tvEmail.text = "Email: ${usuario?.email}"

        // Boton editar
        val btnEditar = findViewById<MaterialButton>(R.id.btnEditar)
        btnEditar.setOnClickListener {
            startActivity(Intent(this, EditarUsuarioActivity::class.java))
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_listas
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pagos -> {
                    startActivity(Intent(this, PagosActivity::class.java)) // o MainActivity
                    true
                }

                R.id.nav_activity -> {
                    startActivity(Intent(this, ActividadesActivity::class.java)) // o MainActivity
                    true
                }

                R.id.nav_settings -> {
                    startActivity(Intent(this, ConfiguracionActivity::class.java)) // o MainActivity
                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, InicioActivity::class.java)) // o MainActivity
                    true
                }

                R.id.nav_listas -> {
                    startActivity(Intent(this, ListadosActivity::class.java)) // o MainActivity
                    true
                }

                else -> true
            }
        }
    }
}