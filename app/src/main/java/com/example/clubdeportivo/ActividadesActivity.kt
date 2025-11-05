package com.example.clubdeportivo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView


class ActividadesActivity : AppCompatActivity() {

    private lateinit var contenedor: LinearLayout
    private lateinit var tvBienvenida: TextView
    private lateinit var btnAgregar: MaterialButton
    private lateinit var bottom: BottomNavigationView

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        // --- refs UI ---
        contenedor   = findViewById(R.id.contenedorActividades)
        btnAgregar   = findViewById(R.id.btnAgregar)
        tvBienvenida = findViewById(R.id.tvBienvenida)
        bottom       = findViewById(R.id.bottomNav)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        tvBienvenida.text = "Bienvenido, $usuario"

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, IngresarActividadActivity::class.java))
        }

        // --- carga y render de actividades ---
        loadActividades()

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_activity
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pagos -> {
                    startActivity(Intent(this, PagosActivity::class.java)) // o MainActivity
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

    private fun loadActividades() {
        val lista = DBHelper(this).obtenerActividades()
        renderActividades(lista)
    }
    private fun renderActividades(lista: List<DBHelper.ActividadCard>) {
        contenedor.removeAllViews()
        val inflater = layoutInflater

        lista.forEach { a ->
            val item = inflater.inflate(R.layout.item_actividades, contenedor, false)

            item.findViewById<TextView>(R.id.tvNombreActividad).text = a.nombre
            item.findViewById<TextView>(R.id.tvProfesor).text =
                "Profesor: " + (a.profesores?.takeIf { it.isNotBlank() } ?: "— (sin asignar)")
            item.findViewById<TextView>(R.id.tvHorarios).text =
                "Día y horario:  " + (a.horarios?.takeIf { it.isNotBlank() } ?: "(sin cargar)")
            item.findViewById<TextView>(R.id.tvPrecio).text =
                "Valor: $ ${a.precio.toInt()} ARS"

            // Acciones del item (si tenés pantallas para esto)
            item.findViewById<Button>(R.id.btnEditar).setOnClickListener {
                startActivity(Intent(this, EditarActividadActivity::class.java).apply {
                    putExtra("id_actividad", a.id)
                })
            }
            item.findViewById<Button>(R.id.btnEliminar).setOnClickListener {
                // TODO: mostrar diálogo y eliminar por a.id (si ya lo implementaste en DBHelper)
            }

            contenedor.addView(item)
        }
    }

}
