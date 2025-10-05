package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class IngresarActividadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingresar_actividad)

        val bottom = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_activity

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true                    // ya estás aquí
                R.id.nav_pagos -> {
                    startActivity(Intent(this, PagosActivity::class.java)) // o MainActivity
                    true
                }
                R.id.nav_activity -> {
                    startActivity(Intent(this, ActividadesActivity::class.java)) // o MainActivity
                    true
                }
//                R.id.nav_listas -> {
//                    startActivity(Intent(this, `plan B`.ListadoSociosActivity::class.java)) // o MainActivity
//                    true
//                }
                else -> false
            }
        }
    }

}