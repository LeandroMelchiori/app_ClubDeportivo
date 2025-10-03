package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView


class ActividadesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        findViewById<MaterialButton>(R.id.btnAgregar).setOnClickListener {
            startActivity(Intent(this, IngresarActividadActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnEditar).setOnClickListener {
            startActivity(Intent(this, EditarActividadActivity::class.java))
        }

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_activity

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_activity -> true                    // ya estás aquí
                R.id.nav_pagos -> {
                    startActivity(Intent(this, PagosActivity::class.java)) // o MainActivity
                    true
                }
                else -> true
            }
        }
    }
}
