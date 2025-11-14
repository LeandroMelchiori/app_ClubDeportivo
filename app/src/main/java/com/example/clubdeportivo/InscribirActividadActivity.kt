package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class InscribirActividadActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var etBuscar: SearchView            // <--- usa el id real de tu buscador (ej: etBuscar)
    private lateinit var tvNombreUsuario: TextView     // tvNombreUsuario
    private lateinit var tvNombreActividad: TextView   // tvNombreActividad
    private lateinit var tvHoraInicio: TextView           // tvHorario
    private lateinit var tvPrecio: TextView            // tvPrecio

    private lateinit var tvIdUsuario: TextView
    // tvIdUsuario
    private lateinit var btnPagar: Button              // btnPagar
    private lateinit var rgMedioPago: RadioGroup       // rgMedioPago



    override fun onCreate(savedInstanceState: Bundle?) {
        val db = DBHelper(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscribir_actividad)

        // Inicializar views
        tvNombreActividad = findViewById<TextView>(R.id.tvNombreActividad)
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

        // Valor int dia de la convertido a texto
        val dias = arrayOf("Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado")
        val diaTxt = when {
            diaActividad in 0..6 -> dias[diaActividad]
            diaActividad in 1..7 -> dias[diaActividad % 7] // 7→0 (Domingo)
            else -> diaActividad.toString()
        }

        // Asignar datos a views
        tvNombreActividad.text = "Actividad: $nombreActividad"
        tvHoraInicio.text = "$diaTxt - $horaInicio hs"
        tvPrecio.text = "Precio: $precio"

        // Buscador
        etBuscar = findViewById(R.id.etBuscar)
        etBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val persona = db.obtenerPersonaPorDni(query)
                if (persona != null) {
                    tvNombreUsuario.text = "Nombre: ${persona.nombre}"
                    tvIdUsuario.text = "DNI: ${persona.dni}"
                    rgMedioPago.isEnabled = true
                } else {
                    toast("Ingresa un DNI valido")
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
            val dni = etBuscar.query.toString()
            val persona = db.obtenerPersonaPorDni(dni)
            if (persona == null) {
                toast("Ingresa un DNI valido")
            }

            if (persona?.esSocio == true){
                toast("Socios no necesitan pagar")
            }

            if (persona != null && persona.esSocio != true) {
                val medioPago = when (rgMedioPago.checkedRadioButtonId) {
                    R.id.rbEfectivo -> "Efectivo"
                    R.id.rbTarjeta -> "Tarjeta"
                    R.id.rbTransferencia -> "Transferencia"
                    else -> {}
                }
            }
            val selectedId = rgMedioPago.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Debe seleccionar una forma de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val formaPago = findViewById<RadioButton>(selectedId).text.toString()
            val insertedId = db.registrarPagoActividadNoSocio(
                dni = persona!!.dni,
                horarioId = idActividad,
                monto = precio,
                medioPago = formaPago
            )

            if (insertedId > 0L) {
                toast("Pago registrado")
                finish() // o limpiar pantalla si preferís
            } else {
                toast("No se pudo registrar el pago")
            }

        }
        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_home
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

                R.id.nav_listas -> {
                    startActivity(Intent(this, ListadosActivity::class.java)) // o MainActivity
                    true
                }

                else -> true
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}