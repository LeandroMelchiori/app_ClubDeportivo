package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class PagoDeCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = DBHelper(this)
        val fechaHoy = LocalDate.now()

        // Datos recuperados de la vista anterior
        val noSocio = intent.getStringExtra("nombre") ?: "nombre"
        val dni = intent.getStringExtra("dni") ?: "dni"
        val ultimoPago = intent.getStringExtra("ultimoPago") ?: "ultimoPago"
        val tipoOperacion = intent.getStringExtra("tipoOperacion") ?: "tipoOperacion"
        val precio = intent.getStringExtra("precio") ?: "precio"
        val esSocio = intent.getBooleanExtra("esSocio", false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_de_cuota)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Fecha encabezado
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val formato = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "AR"))
        val fechaEncabezado = formato.format(Date())
        tvFecha.text = fechaEncabezado.replaceFirstChar { it.uppercase() }

        // Inicializar vistas
        val tvNombre = findViewById<TextView>(R.id.tvNombre)
        val tvDni = findViewById<TextView>(R.id.tvDni)
        val tvTipoOperacion = findViewById<TextView>(R.id.tvTipoOperacion)
        val tvPrecio = findViewById<TextView>(R.id.tvPrecio)
        val btnPagar = findViewById<MaterialButton>(R.id.btnPagar)

        // Asignar datos a las view
        tvNombre.text = "$noSocio"
        tvDni.text = "$dni"
        tvTipoOperacion.text = "$tipoOperacion"
        tvPrecio.text = "Valor: $precio"

        // Forma de pago seleccionada
        val radioGroup = findViewById<RadioGroup>(R.id.rgMediosdePago)
        val selectedId = radioGroup.checkedRadioButtonId

        // Logica de pago
        btnPagar.setOnClickListener {

            val radioGroup = findViewById<RadioGroup>(R.id.rgMediosdePago)
            val selectedId = radioGroup.checkedRadioButtonId

            if (selectedId == -1) {
                Toast.makeText(this, "Debe seleccionar una forma de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formaPago = findViewById<RadioButton>(selectedId).text.toString()

            val monto = precio.toDoubleOrNull()
            if (monto == null) {
                Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (esSocio) {
                // Si ultimo pago esta dentro del mes actual, debe indicar que el pago  del mes ya esta hecho
                val fechaUltimoPago = LocalDate.parse(ultimoPago)
                if (fechaUltimoPago.month == fechaHoy.month) {
                    Toast.makeText(this, "El pago del mes actual ya fue realizado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // 💬 Diálogo de confirmación
                    AlertDialog.Builder(this)
                        .setTitle("Confirmar pago")
                        .setMessage("¿Confirmás registrar el pago de $$monto por \"$formaPago\"?")
                        .setPositiveButton("Sí") { _, _ ->
                            try {
                                val fechaHoy = LocalDate.now().toString()
                                val db = DBHelper(this)
                                db.registrarPagoCuota(dni, monto, formaPago, fechaHoy)
                                Toast.makeText(this, "¡Pago exitoso!", Toast.LENGTH_LONG).show()
                                intent = Intent(this, ListadosActivity::class.java)
                                intent.putExtra("usuario", usuario)
                                startActivity(intent)
                            } catch (e: IllegalArgumentException) {
                                Toast.makeText(this, e.message ?: "Error al realizar el pago", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            } else {
            // 💬 Diálogo de confirmación
            AlertDialog.Builder(this)
                .setTitle("Confirmar pago")
                .setMessage("¿Confirmás registrar el pago de $$monto por \"$formaPago\" y convertir a $noSocio en socio?")
                .setPositiveButton("Sí") { _, _ ->
                    try {
                        val fechaHoy = LocalDate.now().toString()
                        val db = DBHelper(this)
                        val idSocio = db.hacerSocioDesdeNoSocio(dni, monto, formaPago, fechaHoy)
                        Toast.makeText(this, "¡Pago exitoso! Ahora es socio (id $idSocio)", Toast.LENGTH_LONG).show()
                        intent = Intent(this, ListadosActivity::class.java)
                        intent.putExtra("usuario", usuario)
                        startActivity(intent)
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(this, e.message ?: "No se pudo hacer socio", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
            }
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_pagos
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
                else -> true
            }
        }
    }
}