package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class InicioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        findViewById<MaterialButton>(R.id.btnUsuario).setOnClickListener {
            startActivity(Intent(this, NuevoUsuarioActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnInscribir).setOnClickListener {
            startActivity(Intent(this, InscribirActividadActivity::class.java))
        }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home

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