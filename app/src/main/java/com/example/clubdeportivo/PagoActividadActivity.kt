package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class PagoActividadActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var utils: AppUtils
    private lateinit var etBuscar: SearchView            // <--- usa el id real de tu buscador (ej: etBuscar)
    private lateinit var tvNombreUsuario: TextView     // tvNombreUsuario
    private lateinit var tvNombreActividad: TextView   // tvNombreActividad
    private lateinit var tvHoraInicio: TextView           // tvHorario
    private lateinit var tvPrecio: TextView            // tvPrecio
    private lateinit var tvIdUsuario: TextView
    // tvIdUsuario
    private lateinit var btnPagar: Button              // btnPagar
    private lateinit var rgMedioPago: RadioGroup       // rgMedioPago
    private var clienteSeleccionado: DBHelper.PersonaDTO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscribir_actividad)
        db = DBHelper(this)
        utils = AppUtils(this)

        // Inicializar views
        tvNombreActividad = findViewById(R.id.tvNombreActividad)
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvIdUsuario = findViewById(R.id.tvIdUsuario)
        tvHoraInicio = findViewById(R.id.tvHorario)
        tvPrecio = findViewById(R.id.tvPrecio)
        btnPagar = findViewById(R.id.btnPagar)
        rgMedioPago = findViewById(R.id.rgMedioPago)

        // Deshabilitados inicialmente
        btnPagar.isEnabled = false
        rgMedioPago.isEnabled = false

        // Recupera datos de la actividad del intent
        val idActividad = intent.getIntExtra("idActividad", -1)
        val nombreActividad = intent.getStringExtra("nombreActividad") ?: "nombre de la actividad"
        val horaInicio = intent.getStringExtra("horaInicio") ?: "Hora de inicio"
        val diaActividad = intent.getIntExtra("diaActividad", -1)
        val precio = intent.getDoubleExtra("precioActividad", 0.0)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Fecha encabezado
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        tvFecha.text = utils.fechaActualFormato()

        // Valor int del dia convertido a texto
        val diaTxt = utils.diaTexto(diaActividad)

        // Asignar datos a views
        tvNombreActividad.text = "Actividad: $nombreActividad"
        tvHoraInicio.text = "$diaTxt - $horaInicio hs"
        tvPrecio.text = "Precio: $precio"

        // Buscador
        etBuscar = findViewById(R.id.etBuscar)
        etBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val persona = db.buscarPersonaPorDni(query)
                if (persona != null) {
                    clienteSeleccionado = persona
                    tvNombreUsuario.text = "${persona.apellido}, ${persona.nombre}"
                    tvIdUsuario.text = "DNI: ${persona.dni}"
                    rgMedioPago.isEnabled = true
                } else {
                    utils.toast("Ingresa un DNI valido")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        // RadioGroup
        rgMedioPago.setOnCheckedChangeListener { _, checkedId ->
            btnPagar.isEnabled = checkedId != -1
        }

        // Boton Pagar
        btnPagar.setOnClickListener {
            if (clienteSeleccionado == null) {
                utils.toast("Primero busque y seleccione un DNI válido")
                return@setOnClickListener
            } else{
                utils.confirmDialog(
                    "Confirmar pago actividad",
                    "¿Confirmás registrar el pago de $precio por la actividad: $nombreActividad ?"
                ){
                    val ok = pagarActividad(idActividad, precio)
                    if (ok) utils.goTo(InicioActivity::class.java, finishCurrent = true)
                }
            }
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_pagos)
    }
    private fun pagarActividad(idActividad: Int, precio: Double): Boolean {
        // 1) Buscar persona
        val cliente = clienteSeleccionado
        if (cliente == null) {
            utils.toast("No se encontró una persona con ese DNI")
            return false
        }
        if (cliente.esSocio) {
            utils.toast("Los socios no necesitan pagar las actividades")
            return false
        }

        // 2) Validar medio de pago
        val selectedId = rgMedioPago.checkedRadioButtonId
        if (selectedId == -1) {
            utils.toast( "Debe seleccionar una forma de pago")
            return false
        }
        val formaPago = findViewById<RadioButton>(selectedId).text.toString()

        // 3) Registrar pago
        val insertedId = db.registrarPagoActividadNoSocio(
            idCliente = cliente.id,
            horarioId = idActividad,
            monto = precio,
            medioPago = formaPago
        )
        return if (insertedId > 0L) {
            utils.toast("Pago registrado")
            true
        } else {
            utils.toast("No se pudo registrar el pago")
            false
        }
    }
}