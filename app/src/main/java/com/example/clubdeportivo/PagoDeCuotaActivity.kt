package com.example.clubdeportivo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.RadioGroup
import com.google.android.material.button.MaterialButton
import java.time.LocalDate

class PagoDeCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = DBHelper(this)
        val utils = AppUtils(this)
        val fechaHoy = LocalDate.now()

        // Datos recuperados de la vista anterior
        val noSocio = intent.getStringExtra("nombre") ?: "nombre"
        val dni = intent.getStringExtra("dni") ?: "dni"
        val ultimoPago = intent.getStringExtra("ultimoPago") ?: "ultimoPago"
        val tipoOperacion = intent.getStringExtra("tipoOperacion") ?: "tipoOperacion"
        val precio = intent.getStringExtra("precio") ?: "precio"
        val esSocio = intent.getBooleanExtra("esSocio", false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_cuota)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Fecha encabezado
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        tvFecha.text = utils.fechaActualFormato()

        // Inicializar vistas
        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvDni = findViewById<TextView>(R.id.tvDni)
        val tvTipoOperacion = findViewById<TextView>(R.id.tvTipoOperacion)
        val tvPrecio = findViewById<TextView>(R.id.tvPrecio)
        val btnPagar = findViewById<MaterialButton>(R.id.btnPagar)
        val radioGroup = findViewById<RadioGroup>(R.id.rgMediosdePago)

        // Asignar datos a las view
        tvNombre.text = "$noSocio"
        tvDni.text = "$dni"
        tvTipoOperacion.text = "$tipoOperacion"
        tvPrecio.text = "Valor: $precio"

        // Logica de pago
        btnPagar.setOnClickListener {
            val formaPago = utils.getSelectedRadioText(this, radioGroup)
                ?: return@setOnClickListener utils.toast("Debe seleccionar una forma de pago")
            val monto = precio.toDoubleOrNull() ?: return@setOnClickListener utils.toast("Monto inválido")

            // Si el cliente ya es socio, se procede al cobro de cuota mensual
            // sino, se procede con el proceso de hacer socio
            if (esSocio) {
                val fechaUltimoPago = LocalDate.parse(ultimoPago)
                if (fechaUltimoPago.month == fechaHoy.month) {
                    utils.toast("El pago del mes actual ya se encuentra realizado")
                    finish()
                } else {
                    // Confirmar pago de cuota
                    utils.confirmDialog(
                        title = "Confirmar pago",
                        message = "¿Confirmás registrar el pago de $$monto por \"$formaPago\"?"
                    ) {
                        db.registrarPagoCuota(dni, monto, formaPago, fechaHoy.toString())
                        utils.toast("¡Pago exitoso!")
                        utils.goTo(ListadosActivity::class.java, usuario, true)
                    }
                }
            } else {
                // Logica para convertir en socio
                utils.confirmDialog(
                    "Confirmar Pago",
                    "¿Confirmás registrar el pago de $$monto por \"$formaPago\" y convertir a $noSocio en socio?"
                ){
                    db.hacerSocioDesdeNoSocio(dni.toInt(), monto, formaPago, fechaHoy.toString())
                    utils.toast("¡Pago exitoso! Ahora ${noSocio} es socio")
                    utils.goTo(ListadosActivity::class.java, usuario, true)
                }
            }
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_pagos)
    }
}