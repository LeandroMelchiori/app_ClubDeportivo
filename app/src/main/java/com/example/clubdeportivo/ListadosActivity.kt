package com.example.clubdeportivo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListadosActivity : AppCompatActivity() {

    // Tipo de view

    private lateinit var rvNoSocios: RecyclerView
    private lateinit var rvSocios: RecyclerView
    private lateinit var rvVenc: RecyclerView
    private lateinit var noSocioAdapter: NoSocioAdapter
    private lateinit var socioAdapter: SocioAdapter
    private lateinit var vencimientoAdapter: VencimientoAdapter
    lateinit var btnElegirFecha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        // DB Helper
        val db = DBHelper(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listados)

        // Views
        rvNoSocios = findViewById(R.id.rvNoSocios)
        rvSocios   = findViewById(R.id.rvSocios)
        rvVenc     = findViewById(R.id.rvVencimientos)
        btnElegirFecha = findViewById(R.id.btnElegirFecha)

        // Boton elegir fecha
        btnElegirFecha.setOnClickListener {
            val hoy = LocalDate.now()
            val dlg = DatePickerDialog(
                this,
                { _, y, m, d ->
                    val sel = LocalDate.of(y, m + 1, d).toString()
                    val db2 = DBHelper(this)
                    renderVencimientos(db2.obtenerVencimientos(sel))
                },
                hoy.year, hoy.monthValue - 1, hoy.dayOfMonth
            )
            dlg.show()
        }

        // Fecha actual
        val hoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        rvNoSocios.layoutManager = LinearLayoutManager(this)
        rvSocios.layoutManager   = LinearLayoutManager(this)
        rvVenc.layoutManager     = LinearLayoutManager(this)


        //  crear instancias
        noSocioAdapter = NoSocioAdapter()
        socioAdapter   = SocioAdapter()
        vencimientoAdapter    = VencimientoAdapter()

        // Asignar adapters
        rvNoSocios.adapter = noSocioAdapter
        rvSocios.adapter   = socioAdapter
        rvVenc.adapter     = vencimientoAdapter
        rvNoSocios.setHasFixedSize(true)
        rvSocios.setHasFixedSize(true)
        rvVenc.setHasFixedSize(true)

        // Listados
        renderNoSocios(db.obtenerNoSocios())
        renderSocios(db.obtenerSocios())
        renderVencimientos(db.obtenerVencimientos(hoy))

        // Botones listas
        val botonVencimiento: Button = findViewById(R.id.btnListVencimientos)
        val botonSocios: Button = findViewById(R.id.btnListSocios)
        val botonNoSocios: Button = findViewById(R.id.btnListNoSocios)
        botonVencimiento.setOnClickListener {mostrar(rvVenc)}
        botonSocios.setOnClickListener { mostrar(rvSocios)}
        botonNoSocios.setOnClickListener { mostrar(rvNoSocios) }

        // Bottom
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

                R.id.nav_settings -> {
                    startActivity(Intent(this, ConfiguracionActivity::class.java)) // o MainActivity
                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, InicioActivity::class.java)) // o MainActivity
                    true
                }

                else -> true
            }
        }
    }


    fun mostrar(rv: RecyclerView) {
        rvNoSocios.visibility = View.GONE
        rvSocios.visibility   = View.GONE
        rvVenc.visibility     = View.GONE
        rv.visibility         = View.VISIBLE
    }
    private fun renderNoSocios(lista: List<DBHelper.NoSocioCard>) = noSocioAdapter.submitList(lista)
    private fun renderSocios(lista: List<DBHelper.SocioCard>)     = socioAdapter.submitList(lista)
    private fun renderVencimientos(lista: List<DBHelper.VencimientoCard>) = vencimientoAdapter.submitList(lista)

}