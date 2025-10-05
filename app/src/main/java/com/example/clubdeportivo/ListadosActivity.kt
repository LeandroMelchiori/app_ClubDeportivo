package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListadosActivity : AppCompatActivity() {
    lateinit var scrollVenc: ScrollView
    lateinit var scrollSoc: ScrollView
    lateinit var scrollNoSoc: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listados)

        val botonVencimiento: Button = findViewById<Button>(R.id.btnListVencimientos)
        val botonSocios: Button = findViewById<Button>(R.id.btnListSocios)
        val botonNoSocios: Button = findViewById<Button>(R.id.btnListNoSocios)

        scrollVenc = findViewById<ScrollView>(R.id.scrollVencimientos)
        scrollSoc = findViewById<ScrollView>(R.id.scrollSocios)
        scrollNoSoc = findViewById<ScrollView>(R.id.scrollNoSocios)

        botonVencimiento.setOnClickListener { mostrarScrollViewList(scrollVenc) }
        botonSocios.setOnClickListener { mostrarScrollViewList(scrollSoc) }
        botonNoSocios.setOnClickListener { mostrarScrollViewList(scrollNoSoc) }

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

                R.id.nav_settings-> {
                    startActivity(Intent(this, ConfiguracionActivity::class.java)) // o MainActivity
                    true
                }

                R.id.nav_home-> {
                    startActivity(Intent(this, InicioActivity::class.java)) // o MainActivity
                    true
                }
                else -> true
            }
        }
    }
    fun onVerMasClick(v: View) {
        startActivity(Intent(this, VerMasActivity::class.java))
    }

    private fun mostrarScrollViewList(scrollMenu: ScrollView) {
        scrollVenc.visibility = View.GONE
        scrollSoc.visibility  = View.GONE
        scrollNoSoc.visibility = View.GONE

        scrollMenu.visibility = View.VISIBLE
    }
}