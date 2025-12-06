package com.example.clubdeportivo

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class VerMasActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_mas)

        // Helpers
        db = DBHelper(this)
        utils = AppUtils(this)

        // Encabezado
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        tvFecha.text = utils.fechaActualFormato()

        // Recupera dni del intent y busca a la persona en la BBDD
        val dniUsuario = intent.getStringExtra("dni") ?: ""
        if (dniUsuario.isEmpty()) {
            utils.goTo(ListadosActivity::class.java)
            utils.toast("Error al cargar el cliente")
        }
        val cliente = db.buscarPersonaPorDni(dniUsuario)

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
        tvTelefono.text = "Telefono: ${cliente.telefono}"
        tvDireccion.text = "Domicilio: ${cliente.direccion}"
        tvFechaNacimiento.text = "Fecha de nacimiento: ${cliente.fecha_nac}"
        tvEmail.text = "Email: ${cliente.email}"
        if (cliente.esSocio) {
            tvIdTipoSocio.text = "Socio nº: ${cliente.id}"
        } else {
        tvIdTipoSocio.text = "NoSocio nª: ${cliente.id}"}

        // Boton editar
        val btnEditar = findViewById<MaterialButton>(R.id.btnEditar)
        btnEditar.setOnClickListener {
            utils.goTo(
                EditarUsuarioActivity::class.java,
                true,
                "dni" to cliente.dni)
        }

        // Boton Eliminar
        val btnEliminar: Button = findViewById(R.id.btnEliminar)
        btnEliminar.setOnClickListener {
            // Confirmacion
            utils.confirmDialog(
                "Confirmar eliminación",
                "¿Seguro que querés eliminar a esta persona? Esta acción no se puede deshacer."
            ) {
                val ok = db.eliminarPersonaPorId(cliente.id.toString()) // ← clave
                if (ok) {
                    utils.goTo(
                        ListadosActivity::class.java,
                        true,
                        "usuario" to usuario)
                    utils.toast("Eliminado correctamente")
                } else {
                    utils.toast("No se pudo eliminar")
                    finish()
                }
            }
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_listas)
    }
}