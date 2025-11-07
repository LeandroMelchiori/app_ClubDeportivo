package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import java.time.DayOfWeek
import java.time.LocalDate

class InicioActivity : AppCompatActivity() {
    data class ActividadHoy(
        val id: Int,
        val nombre: String,
        val dia: Int,
        val horaInicio: String,
        val horaFin: String,
        val precio: Double
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        // Dia de la semana
        val db = DBHelper(this)
        val diaHoy = LocalDate.now().dayOfWeek.value % 7  // Lunes=1 ... Domingo=7 → ajustamos a 0–6

        val actividades = db.obtenerActividadesDelDia(diaHoy) // devuelve List<ActividadHoy>

        // Renderiza la lista de actividades del día
        renderActividadesHoy(actividades)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Boton nuevo usuario
        val btnUsuario = findViewById<MaterialButton>(R.id.btnUsuario)
        btnUsuario.setOnClickListener {
            startActivity(Intent(this, NuevoUsuarioActivity::class.java))
        }

        // Bottom
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

                R.id.nav_settings -> {
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

    // Renderiza la lista de actividades del día con el item de tarjeta
    private fun renderActividadesHoy(actividades: List<ActividadHoy>) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorActividadesHoy)
        contenedor.removeAllViews()

        val inflater = LayoutInflater.from(this)

        // Actividades del día
        actividades.forEach { act ->
            val view = inflater.inflate(R.layout.item_actividad_hoy, contenedor, false)
            val tvTitulo  = view.findViewById<TextView>(R.id.tvTitulo)
            val ivIcono   = view.findViewById<ImageView>(R.id.ivIcono)
            val btnAccion = view.findViewById<ImageButton>(R.id.btnAccion)
            tvTitulo.text = "${act.horaInicio} - ${act.nombre}"

            btnAccion.setOnClickListener {
                intent = Intent(this, InscribirActividadActivity::class.java)
                intent.putExtra("idActividad", act.id)
                intent.putExtra("nombreActividad", act.nombre)
                intent.putExtra("precioActividad", act.precio)
                intent.putExtra("diaActividad", act.dia)
                intent.putExtra("horaInicio", act.horaInicio)
                startActivity(intent)
            }

            contenedor.addView(view)
        }
    }

}