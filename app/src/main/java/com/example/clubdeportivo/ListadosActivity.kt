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
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListadosActivity : AppCompatActivity() {

    // Tipo de view
    lateinit var scrollVencimientos: ScrollView
    lateinit var scrollSocios: ScrollView
    lateinit var scrollNoSocios: ScrollView
    lateinit var contenedorNoSocios: LinearLayout
    lateinit var contenedorVencimientos: LinearLayout
    lateinit var contenedorSocios: LinearLayout
    lateinit var btnElegirFecha: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listados)

        btnElegirFecha = findViewById(R.id.btnElegirFecha)
        btnElegirFecha.setOnClickListener {
            val hoy = LocalDate.now()
            val dlg = DatePickerDialog(
                this,
                { _, y, m, d ->
                    val sel = LocalDate.of(y, m + 1, d).toString()
                    val db2 = DBHelper(this)
                    renderVencimientos(db2.obtenerVencimientos(sel))
                    mostrarScrollViewList(scrollVencimientos)
                },
                hoy.year, hoy.monthValue - 1, hoy.dayOfMonth
            )
            dlg.show()
        }

        val hoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE) // "2025-11-02"

        // DB Helper
        val db = DBHelper(this)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Scrollview listas
        scrollVencimientos = findViewById(R.id.scrollVencimientos)
        scrollSocios = findViewById(R.id.scrollSocios)
        scrollNoSocios = findViewById(R.id.scrollNoSocios)

        // Contenedores
        contenedorNoSocios = findViewById(R.id.contenedorNoSocios)
        contenedorVencimientos = findViewById(R.id.contenedorVencimientos)   // existe en el XML
        contenedorSocios  = findViewById(R.id.contenedorSocios)          // <— añadí este id si aún no está


        // Listado no socios
        val tarjetas = db.obtenerNoSocios()
        renderNoSocios(tarjetas)
        mostrarScrollViewList(scrollNoSocios)    // se muestra de entrada

        // Listado socios
        renderSocios(db.obtenerSocios())

        // Listado vencimientos
        renderVencimientos(db.obtenerVencimientos(hoy))

        // Botones listas
        val botonVencimiento: Button = findViewById(R.id.btnListVencimientos)
        val botonSocios: Button = findViewById(R.id.btnListSocios)
        val botonNoSocios: Button = findViewById(R.id.btnListNoSocios)
        botonVencimiento.setOnClickListener { mostrarScrollViewList(scrollVencimientos) }
        botonSocios.setOnClickListener { mostrarScrollViewList(scrollSocios) }
        botonNoSocios.setOnClickListener { mostrarScrollViewList(scrollNoSocios) }

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

    fun onVerMasClick(v: View) {
        startActivity(Intent(this, VerMasActivity::class.java))
    }

    private fun mostrarScrollViewList(scrollMenu: ScrollView) {
        scrollVencimientos.visibility = View.GONE
        scrollSocios.visibility = View.GONE
        scrollNoSocios.visibility = View.GONE

        scrollMenu.visibility = View.VISIBLE
    }
    private fun renderSocios(lista: List<DBHelper.SocioCard>) {
        contenedorSocios.removeAllViews()
        val inflater = layoutInflater
        lista.forEach { s ->
            val item = inflater.inflate(R.layout.item_nosocio, contenedorSocios, false)
            item.findViewById<TextView>(R.id.tvNombre).text = "${s.apellido}, ${s.nombre}"
            item.findViewById<TextView>(R.id.tvDni).text = "#${s.dni}"
            item.findViewById<TextView>(R.id.tvEstado).text = if (s.ultimoPago != null) "Socio Activo" else "Revisar cuota"
            item.findViewById<TextView>(R.id.tvUltimoPago).text = if (s.ultimoPago != null) "Último pago: ${s.ultimoPago}" else "Sin pagos registrados"
            item.findViewById<Button>(R.id.btnAccion).text = "Cuota"
            item.findViewById<Button>(R.id.btnAccion).setOnClickListener {
                //
            }
            item.findViewById<Button>(R.id.btnVerMas).setOnClickListener {
                intent = Intent(this, VerMasActivity::class.java)
                intent.putExtra("dni", s.dni)
                startActivity(intent)
            }
            contenedorSocios.addView(item)
        }
    }
    private fun renderVencimientos(lista: List<DBHelper.VencimientoCard>) {
        contenedorVencimientos.removeAllViews()
        val inflater = layoutInflater
        lista.forEach { v ->
            val item = inflater.inflate(R.layout.item_nosocio, contenedorVencimientos, false)
            item.findViewById<TextView>(R.id.tvNombre).text = "${v.apellido}, ${v.nombre}"
            item.findViewById<TextView>(R.id.tvDni).text = "#${v.dni}"
            item.findViewById<TextView>(R.id.tvEstado).text = "Vence: ${v.fechaVenc}"
            item.findViewById<TextView>(R.id.tvUltimoPago).text = if (v.ultimoPago != null) "Último pago: ${v.ultimoPago}" else "Sin pagos registrados"
            item.findViewById<Button>(R.id.btnAccion).text = "Pagar"


            item.findViewById<Button>(R.id.btnAccion).setOnClickListener {
                // TODO: abrir flujo de pago de cuota
            }
            item.findViewById<Button>(R.id.btnVerMas).setOnClickListener {
                intent = Intent(this, VerMasActivity::class.java)
                intent.putExtra("dni", v.dni)
                startActivity(intent)
            }
            contenedorVencimientos.addView(item)
        }
    }
    private fun renderNoSocios(lista: List<DBHelper.NoSocioCard>) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorNoSocios)
        contenedor.removeAllViews()
        val inflater = layoutInflater

        lista.forEach { ns ->
            val item = inflater.inflate(R.layout.item_nosocio, contenedor, false)

            item.findViewById<TextView>(R.id.tvNombre).text = "${ns.apellido}, ${ns.nombre}"
            item.findViewById<TextView>(R.id.tvDni).text = "#${ns.dni}"
            item.findViewById<TextView>(R.id.tvEstado).text = "Activo"  // podés cambiarlo si querés otra lógica
            item.findViewById<TextView>(R.id.tvUltimoPago).text =
                "Ultima pago: ${ns.ultimaPago ?: "-"}"

            item.findViewById<Button>(R.id.btnAccion).setOnClickListener {
                intent = Intent(this, PagoDeCuotaActivity::class.java)
                intent.putExtra("dni", ns.dni)
                intent.putExtra("nombre", "${ns.apellido}, ${ns.nombre}")
                intent.putExtra("tipoOperacion", "Ser socio")
                intent.putExtra("precio", "30000")
                startActivity(Intent(intent))
            }
            item.findViewById<Button>(R.id.btnVerMas).setOnClickListener {
                intent = Intent(this, VerMasActivity::class.java)
                intent.putExtra("dni", ns.dni)
                startActivity(intent)
            }

            contenedor.addView(item)
        }
    }

}