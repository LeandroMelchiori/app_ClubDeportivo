package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PagosActivity : AppCompatActivity() {      // o MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_pagos)  // <-- layout de pagos

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_pagos

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
                R.id.nav_listas -> {
                    startActivity(Intent(this, ListadoSociosActivity::class.java)) // o MainActivity
                    true
                }
                else -> true
            }
        }
    }
}
