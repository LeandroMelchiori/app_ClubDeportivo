package com.example.clubdeportivo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ConfiguracionActivity : AppCompatActivity() {
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)
        utils = AppUtils(this)

        // Encabezado
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        tvFecha.text = utils.fechaActualFormato()

        // Boton editar admin
        val btnEditar = findViewById<MaterialButton>(R.id.btnEditar)
        btnEditar.isEnabled = false
        btnEditar.setOnClickListener {
            utils.goTo(EditarAdminActivity::class.java)
        }

        // Boton nuevo admin
        val btnNuevo = findViewById<MaterialButton>(R.id.btnNuevo)
        btnNuevo.isEnabled = false
        btnNuevo.setOnClickListener {
            // utils.goTo(NuevoAdminActivity::class.java)
        }

        // Boton cerrar sesion
        val btnSalir = findViewById<MaterialButton>(R.id.btnCerrarSesion)
        btnSalir.setOnClickListener {
            // Confirmacion
            utils.confirmDialog(
                "Cerrar sesion",
                "¿Estas seguro que quieres cerrar sesion?") {
                    utils.goTo(LoginActivity::class.java)
            }
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_settings)
    }
}