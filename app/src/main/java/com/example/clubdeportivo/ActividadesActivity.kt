package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView


class ActividadesActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var tvBienvenida: TextView
    private lateinit var btnAgregar: MaterialButton
    private lateinit var bottom: BottomNavigationView
    private lateinit var etBuscar: SearchView
    private lateinit var db: DBHelper
    private lateinit var adapter: ActividadCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        db = DBHelper(this)

        // --- refs UI ---
        btnAgregar   = findViewById(R.id.btnAgregar)
        tvBienvenida = findViewById(R.id.tvBienvenida)
        bottom       = findViewById(R.id.bottomNav)
        etBuscar     = findViewById(R.id.etBuscar)
        rv           = findViewById(R.id.contenedorActividades)

        // Agregar horario
        btnAgregar.setOnClickListener {
            startActivity(Intent(this, IngresarActividadActivity::class.java))}


        adapter = ActividadCardAdapter(
            onEditar = { act ->
                // TODO: navega a tu pantalla de edición con act.id
                // startActivity(Intent(this, EditarActividadActivity::class.java).putExtra("id", act.id))
            },
            onEliminar = { act ->
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar actividad")
                    .setMessage("Se eliminará \"${act.nombre}\" en el horario del ${act.etiquetaHorario} ¿Continuar?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        val ok = db.darDeBajaHorario(act.idDiaHorario)
                        if (ok) {
                            Toast.makeText(this, "Actividad eliminada", Toast.LENGTH_SHORT).show()
                            // refrescá la lista respetando el filtro actual del SearchView
                            recargarLista(findViewById<SearchView>(R.id.etBuscar).query?.toString())
                        } else {
                            Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)
        rv.adapter = adapter

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        tvBienvenida.text = "Bienvenido, $usuario"

        // Carga inicial
        recargarLista(null)

        // Buscador en vivo
        etBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtro = newText?.trim().orEmpty()
                recargarLista(if (filtro.isEmpty()) null else filtro)
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                val filtro = query?.trim().orEmpty()
                recargarLista(if (filtro.isEmpty()) null else filtro)
                return true
            }
        })

        // Bottom
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
    private fun recargarLista(filtro: String?) {
        val lista = if (filtro.isNullOrBlank())
            db.obtenerActividadesPorHorario()
        else
            db.buscarActividadesPorNombre(filtro)
        adapter.submitList(lista)
    }
}
