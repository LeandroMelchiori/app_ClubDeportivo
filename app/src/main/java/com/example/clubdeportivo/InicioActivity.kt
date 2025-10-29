package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)

        // Recupera el nombre de usuario del intent
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"

        // Muestra el nombre de usuario en el TextView
        tvBienvenida.text = "Bienvenido, $usuario"

        // Cartel emergente al iniciar la vista
        Snackbar.make(findViewById(android.R.id.content),
            "Sesion iniciada...", Snackbar.LENGTH_SHORT)
            .show()

        // Boton nuevo usuario
        findViewById<MaterialButton>(R.id.btnUsuario).setOnClickListener {
            startActivity(Intent(this, NuevoUsuarioActivity::class.java))
        }

        // Botón Inscribir actividad
        findViewById<MaterialButton>(R.id.btnInscribir).setOnClickListener {
            startActivity(Intent(this, InscribirActividadActivity::class.java))
        }

        // Mantiene seleccionado el icono de la vista actual
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home

        // BottomNavigationView
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

                R.id.nav_settings-> {
                    startActivity(Intent(this, ConfiguracionActivity::class.java)) // o MainActivity
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