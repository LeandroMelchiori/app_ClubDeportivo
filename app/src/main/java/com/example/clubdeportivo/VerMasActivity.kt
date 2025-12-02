package com.example.clubdeportivo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class VerMasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mas)

        // DB Helper
        val db = DBHelper(this)

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Recupera dni del intent y busca a la persona en la BBDD
        val dniUsuario = intent.getStringExtra("dni") ?: "dni"
        val cliente = db.obtenerPersonaPorDni(dniUsuario)

        //Inicializar vistas
        val tvNombreCompleto = findViewById<TextView>(R.id.tvNombreUsuario)
        val tvDNI = findViewById<TextView>(R.id.tvDNI)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvDireccion = findViewById<TextView>(R.id.tvDireccion)
        val tvTelefono = findViewById<TextView>(R.id.tvTelefono)
        val tvFechaNacimiento = findViewById<TextView>(R.id.tvFechaNacimiento)
        val tvIdTipoSocio = findViewById<TextView>(R.id.tvIdTipoSocio)


        // Reemplaza datos en las view
        tvNombreCompleto.text = "${cliente?.nombre}, ${cliente?.apellido} "
        tvDNI.text = "DNI: ${cliente!!.dni}"
        tvTelefono.text = "Telefono: ${cliente?.telefono}"
        tvDireccion.text = "Domicilio: ${cliente?.direccion}"
        tvFechaNacimiento.text = "Fecha de nacimiento: ${cliente?.fecha_nac}"
        tvEmail.text = "Email: ${cliente?.email}"
        if (cliente.esSocio) {
            tvIdTipoSocio.text = "Socio nº: ${cliente.id}"
        } else {
        tvIdTipoSocio.text = "NoSocio nª: ${cliente.id}"}

        // Boton editar
        val btnEditar = findViewById<MaterialButton>(R.id.btnEditar)
        btnEditar.setOnClickListener {
            val intent = Intent(this, EditarUsuarioActivity::class.java)
            intent.putExtra("id", cliente!!.id)
            intent.putExtra("dni", cliente.dni)
            intent.putExtra("esSocio", cliente.esSocio)
            startActivity(intent)
        }

        // Boton Eliminar
        val btnEliminar: Button = findViewById(R.id.btnEliminar)
        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar registro")
                .setMessage("¿Seguro que querés eliminar a esta persona? Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar") { _, _ ->
                    val ok = db.eliminarPersonaPorId(cliente!!.id.toString()) // ← clave
                    if (ok) {
                        Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_SHORT).show()
                        val data = Intent().putExtra("dniEliminado", cliente.dni)
                        setResult(Activity.RESULT_OK, data)
                        intent = Intent(this, ListadosActivity::class.java)
                        intent.putExtra("usuario", usuario)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "No se pudo eliminar", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
                .show()
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_listas
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