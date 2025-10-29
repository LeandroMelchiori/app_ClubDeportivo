package com.example.clubdeportivo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class pruebasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pruebas)

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home

        val rvLista = findViewById<RecyclerView>(R.id.rvLista)
        rvLista.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        val datos = mutableListOf(
            "Dato 1",
            "Dato 2",
            "Dato 3",
            "Dato 4",
            "Dato 1",
            "Dato 2",
            "Dato 3",
            "Dato 4",
            "Dato 1",
            "Dato 2",
            "Dato 3",
            "Dato 4",
            "Dato 1",
            "Dato 2",
            "Dato 3",
            "Dato 4",
        )

        val adapter = DatoAdapter(datos)
        rvLista.adapter = adapter

        val btnAgregar = findViewById<MaterialButton>(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            val input = EditText(this)
            AlertDialog.Builder(this)
                .setTitle("Agregar Dato")
                .setView(input)
                .setMessage("Ingrese el nombre del producto")
                .setView(input)
                .setPositiveButton("Agregar") { _, _ ->
                    val nuevoDato = input.text.toString()
                    datos.add(nuevoDato)
                    adapter.notifyItemInserted(datos.size - 1)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Bottom
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