package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PagosActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private var mesActual: Int = 0
    private var anioActual: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_pagos)

        db = DBHelper(this)

        // --------- Usuario ----------
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"


        // --------- Fecha de hoy ----------
        val calendar = Calendar.getInstance()
        mesActual = calendar.get(Calendar.MONTH) + 1      // 1..12
        anioActual = calendar.get(Calendar.YEAR)

        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val formatoFecha = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "AR"))
        tvFecha.text = formatoFecha.format(calendar.time)

        // --------- Referencias a los TextView del resumen ----------
        val tvMes = findViewById<TextView>(R.id.tvMes)
        val tvNoSocios = findViewById<TextView>(R.id.tvNoSocios)
        val tvSocios = findViewById<TextView>(R.id.tvSocios)
        val tvTotalClientes = findViewById<TextView>(R.id.tvTotalClientes)
        val tvMontoCuotas = findViewById<TextView>(R.id.tvMontoCuotas)
        val tvMontoActividades = findViewById<TextView>(R.id.tvMontoActividades)
        val tvIngresosTotales = findViewById<TextView>(R.id.tvIngresosTotales)
        val btnMesAnterior = findViewById<TextView>(R.id.btnMesAnterior)
        val btnMesSiguiente = findViewById<TextView>(R.id.btnMesSiguiente)

        fun nombreMes(mes: Int): String {
            val meses = arrayOf(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
            )
            return meses[mes - 1]
        }

        // Cargar el mes actual al entrar
        fun cargarMes() {
            val resumen = db.obtenerResumenPagosMes(anioActual, mesActual)

            tvMes.text = nombreMes(mesActual)
            tvNoSocios.text = "No Socios : ${resumen.cantNoSocios}"
            tvSocios.text = "Socios: ${resumen.cantSocios}"
            tvTotalClientes.text = "Total clientes: ${resumen.totalClientes}"
            tvMontoCuotas.text = "Monto cuotas: $${resumen.montoCuotas}"
            tvMontoActividades.text = "Monto Actividades: $${resumen.montoActividades}"
            tvIngresosTotales.text = "Ingresos Totales: $${resumen.ingresosTotales}"
        }
        cargarMes()

        val calHoy = Calendar.getInstance()
        val mesHoy = calHoy.get(Calendar.MONTH) + 1
        val anioHoy = calHoy.get(Calendar.YEAR)

        btnMesAnterior.setOnClickListener {
            mesActual--
            if (mesActual < 1) {
                mesActual = 12
                anioActual--
            }
            cargarMes()
        }

        btnMesSiguiente.setOnClickListener {
            // Solo dejamos avanzar hasta el mes actual del año actual
            val esMismoAnio = (anioActual == anioHoy)
            val puedeAvanzar =
                (anioActual < anioHoy) || (esMismoAnio && mesActual < mesHoy)

            if (puedeAvanzar) {
                mesActual++
                if (mesActual > 12) {
                    mesActual = 1
                    anioActual++
                }
                cargarMes()
            }
        }


        // --------- Bottom nav ----------
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_pagos
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
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

                R.id.nav_listas -> {
                    val intent = Intent(this, ListadosActivity::class.java)
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
}
