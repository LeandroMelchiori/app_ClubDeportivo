package com.example.clubdeportivo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NuevoHorarioActividadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingresar_actividad)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Fecha encabezado
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val formato = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "AR"))
        val fechaHoy = formato.format(Date())
        tvFecha.text = fechaHoy.replaceFirstChar { it.uppercase() }

        // inputs
        val spDia        = findViewById<Spinner>(R.id.spDia)
        val spHoraInicio = findViewById<Spinner>(R.id.spHoraInicio)
        val spHoraFin    = findViewById<Spinner>(R.id.spHoraFin)
        val spActividad  = findViewById<Spinner>(R.id.spActividad)
        val spProfesor   = findViewById<Spinner>(R.id.spProfesor)

        // Adapters
        spDia.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DIAS)
        val actividades = cargarActividades(this)
        spActividad.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, actividades)
        val profesores = cargarProfesores(this)
        spProfesor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, profesores)

        // Adapters para los spinners de hora
        val slots = buildSlots30min(6, 23) // 06:00 a 23:30
        val adpSlots = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, slots)
        spHoraInicio.adapter = adpSlots
        spHoraFin.adapter    = adpSlots

        // If Listas vacias
        if (actividades.isEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("⛔ No hay actividades"))
            spActividad.adapter = adapter
            spActividad.isEnabled = false
        }
        if (profesores.isEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("⛔ No hay profesores"))
            spProfesor.adapter = adapter
            spProfesor.isEnabled = false
        }

        // Botón registrar
        val btnIngresar = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {

            val actividad = spActividad.selectedItem as ActividadItem
            val profesor  = spProfesor.selectedItem as ProfesorItem
            val dia = spDia.selectedItemPosition
            val hi = hhmmToMin(spHoraInicio.selectedItem as String)
            val hf = hhmmToMin(spHoraFin.selectedItem as String)

            if (hf <= hi) {
                Toast.makeText(this, "El horario de fin debe ser mayor al de inicio", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar horario")
                .setMessage("¿Confirmar nuevo horario de ${actividad.nombre}?")
                .setPositiveButton("Sí") { _, _ ->
                    try {
                        val inserted = DBHelper(this).insertarHorario(actividad.id, profesor.dni, dia, hi, hf)
                        if (inserted == -1L) {
                            Toast.makeText(this, "Ya existe ese horario para ese profesor en esa actividad", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Actividad registrada correctamente", Toast.LENGTH_LONG).show()
                            intent = Intent(this, ActividadesActivity::class.java)
                            intent.putExtra("usuario", usuario)
                            startActivity(intent)
                        }
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(this, e.message ?: "Error al insertar horario", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Bottom
        val bottom = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_activity
        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pagos -> {
                    val intent = Intent(this, ResumenMensualActivity::class.java)
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
                else -> false
            }
        }
    }

    // Cargas de actividades y profesores
    private fun cargarActividades(ctx: Context): List<ActividadItem> {
        val db = DBHelper(ctx).readableDatabase
        val lista = mutableListOf<ActividadItem>()
        db.rawQuery("SELECT id_actividad, nombre FROM actividades ORDER BY nombre", null).use { c ->
            while (c.moveToNext()) {
                lista += ActividadItem(c.getLong(0), c.getString(1))
            }
        }
        db.close()
        return lista
    }
    private fun cargarProfesores(ctx: Context): List<ProfesorItem> {
        val db = DBHelper(ctx).readableDatabase
        val lista = mutableListOf<ProfesorItem>()
        db.rawQuery(
            "SELECT dni, nombre, apellido FROM profesores WHERE IFNULL(activo,1)=1 ORDER BY apellido, nombre",
            null
        ).use { c ->
            while (c.moveToNext()) {
                lista += ProfesorItem(
                    dni = c.getString(0),
                    nombre = c.getString(1),
                    apellido = c.getString(2)
                )
            }
        }
        db.close()
        return lista
    }

    // Herramientas
    private val DIAS = listOf("Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado")
    private fun buildSlots30min(
        startHour: Int = 6,
        endHour: Int = 23,
        includeEndHalf: Boolean = true
    ): List<String> {
        val out = mutableListOf<String>()
        var m = startHour * 60
        val last = endHour * 60 + if (includeEndHalf) 30 else 0
        while (m <= last) {
            val h = m / 60
            val mm = m % 60
            out += String.format("%02d:%02d", h, mm)
            m += 30
        }
        return out
    }

    private fun hhmmToMin(hhmm: String): Int {
        val (h, m) = hhmm.split(":").map { it.toInt() }
        return h * 60 + m
    }

    // Modelos de datos
    data class ActividadItem(val id: Long, val nombre: String) {
        override fun toString() = nombre  // sólo muestra el nombre
    }
    data class ProfesorItem(val dni: String, val nombre: String, val apellido: String) {
        override fun toString() = "$nombre $apellido" // sólo nombre y apellido
    }
}