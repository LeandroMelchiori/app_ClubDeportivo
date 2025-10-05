package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView


class ListadoSociosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_socios)

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_activity

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_listas -> true
                R.id.nav_pagos -> {
                    startActivity(Intent(this, PagosActivity::class.java))
                    true
                }
                R.id.nav_activity -> {
                    startActivity(Intent(this, ActividadesActivity::class.java))
                    true
                }
                else -> true
            }
        }
    }
}
