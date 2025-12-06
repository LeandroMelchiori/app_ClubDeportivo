package com.example.clubdeportivo

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class ResumenMensualActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private var mesActual: Int = 0
    private var anioActual: Int = 0
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen_mensual)

        // Helpers
        utils = AppUtils(this)
        db = DBHelper(this)

        // Encabezado
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        tvFecha.text = utils.fechaActualFormato()

        // --------- Fecha de hoy ----------
        val calendar = Calendar.getInstance()
        mesActual = calendar.get(Calendar.MONTH) + 1      // 1..12
        anioActual = calendar.get(Calendar.YEAR)


        // --------- Referencias a los TextView del resumen ----------
        val tvMes = findViewById<TextView>(R.id.tvMes)
        val tvNoSocios = findViewById<TextView>(R.id.tvNoSocios)
        val tvSocios = findViewById<TextView>(R.id.tvSocios)
        val tvTotalClientes = findViewById<TextView>(R.id.tvTotalClientes)
        val tvMontoCuotas = findViewById<TextView>(R.id.tvMontoCuotas)
        val tvMontoActividades = findViewById<TextView>(R.id.tvMontoActividades)
        val tvIngresosTotales = findViewById<TextView>(R.id.tvIngresosTotales)
        val btnMesAnterior = findViewById<ImageButton>(R.id.btnMesAnterior)
        val btnMesSiguiente = findViewById<ImageButton>(R.id.btnMesSiguiente)

        // Cargar el mes actual al entrar
        fun cargarMes() {
            val resumen = db.obtenerResumenPagosMes(anioActual, mesActual)

            tvMes.text = " ${nombreMes(mesActual)} $anioActual"
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

        // --------- Navegacion meses ----------
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
        utils.setupBottomNav(bottom, usuario, R.id.nav_pagos)
    }
    fun nombreMes(mes: Int): String {
        val meses = arrayOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )
        return meses[mes - 1]
    }
}
