package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class InscribirActividadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscribir_actividad)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Recupera datos de la actividad del intent
        val idActividad = intent.getIntExtra("idActividad", -1)
        val nombreActividad = intent.getStringExtra("nombreActividad") ?: "nombre de la actividad"
        val horaInicio = intent.getStringExtra("horaInicio") ?: "Hora de inicio"
        val diaActividad = intent.getIntExtra("diaActividad", -1)
        val precio = intent.getDoubleExtra("precioActividad", 0.0)


        // Inicializar views
        val tvNombreActividad = findViewById<TextView>(R.id.tvNombreActividad)
        val tvHoraInicio = findViewById<TextView>(R.id.tvHorario)
        val tvPrecio = findViewById<TextView>(R.id.tvPrecio)

        // InscribirActividadActivity.kt
        val dias = arrayOf("Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado")
        val diaTxt = when {
            diaActividad in 0..6 -> dias[diaActividad]
            diaActividad in 1..7 -> dias[diaActividad % 7] // 7→0 (Domingo)
            else -> diaActividad.toString()
        }

        // Asignar datos a views
        tvNombreActividad.text = "Actividad: $nombreActividad"
        tvHoraInicio.text = "$diaTxt - $horaInicio hs"
        tvPrecio.text = "Precio: $precio"


        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pagos -> {
                    startActivity(Intent(this, PagosActivity::class.java).apply {  }) // o MainActivity
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