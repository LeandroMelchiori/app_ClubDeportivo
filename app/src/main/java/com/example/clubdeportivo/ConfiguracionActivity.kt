package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ConfiguracionActivity : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        val btnSalir = findViewById<MaterialButton>(R.id.btnCerrarSesion)

        // Mantiene seleccionado el icono de configuracion
        bottom.selectedItemId = R.id.nav_settings

        // Boton editar admin
        findViewById<MaterialButton>(R.id.btnEditar).setOnClickListener {
            startActivity(Intent(this, EditarAdminActivity::class.java))
        }

        // Boton cerrar sesion
        btnSalir.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar sesion")
                .setMessage("¿Estas seguro que quieres cerrar sesion?")
                .setPositiveButton("Si") { _, _ -> startActivity(Intent(this, MainActivity::class.java)) }
                .setNegativeButton("No", null)
                .show()
        }


        // Bottom
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

                R.id.nav_home-> {
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