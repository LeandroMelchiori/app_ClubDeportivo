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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListadosActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var hoyISO: String
    private lateinit var rvNoSocios: RecyclerView
    private lateinit var rvSocios: RecyclerView
    private lateinit var rvVenc: RecyclerView
    private lateinit var tvNombreLista: TextView
    private lateinit var noSocioAdapter: NoSocioAdapter
    private lateinit var socioAdapter: SocioAdapter
    private lateinit var vencimientoAdapter: VencimientoAdapter
    private lateinit var verMasLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        // DB Helper
        verMasLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                refreshVisibleList()
            }
        }
        val db = DBHelper(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listados)

        // Views
        rvNoSocios = findViewById(R.id.rvNoSocios)
        rvSocios   = findViewById(R.id.rvSocios)
        rvVenc     = findViewById(R.id.rvVencimientos)
        tvNombreLista = findViewById(R.id.tvNombreLista)

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
        botonVencimiento.setOnClickListener {
            mostrar(rvVenc)
            tvNombreLista.text = "Listado Vencimientos"
        }
        botonSocios.setOnClickListener {
            mostrar(rvSocios)
            tvNombreLista.text = "Listado Socios"
        }
        botonNoSocios.setOnClickListener {
            mostrar(rvNoSocios)
            tvNombreLista.text = "Listado No Socios"
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_listas
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pagos -> {
                    val intent = Intent(this, PagosActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }

                R.id.nav_activity -> {
                    val intent = Intent(this, ActividadesActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }

                R.id.nav_settings -> {
                    val intent = Intent(this, ConfiguracionActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
                    true
                }

                R.id.nav_home -> {
                    val intent = Intent(this, InicioActivity::class.java)
                    intent.putExtra("usuario", usuario)
                    startActivity(intent)
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
    private fun refreshVisibleList() {
        val rvSocios        = findViewById<RecyclerView>(R.id.rvSocios)
        val rvNoSocios      = findViewById<RecyclerView>(R.id.rvNoSocios)
        val rvVencimientos  = findViewById<RecyclerView>(R.id.rvVencimientos)
        when {
            rvSocios.visibility == View.VISIBLE ->
                renderSocios(db.obtenerSocios())

            rvNoSocios.visibility == View.VISIBLE ->
                renderNoSocios(db.obtenerNoSocios())

            rvVencimientos.visibility == View.VISIBLE ->
                renderVencimientos(db.obtenerVencimientos(hoyISO))

            else ->
                renderNoSocios(db.obtenerNoSocios()) // fallback
        }
    }
}
