package com.example.clubdeportivo

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class LoginActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Helpers
        db = DBHelper(this)
        utils = AppUtils(this)

        // Variables
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etContraseña)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        // Logica inicio de sesion
        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val password = etPassword.text.toString()

            // Validacion campos en blanco
            if (usuario.isEmpty() || password.isEmpty()) {
                utils.toast("Por favor, ingrese usuario y contraseña")
            }
            // Validacion usuario correcto
            else if (usuario == "admin" && password == "admin"
                || usuario == "charlie" && password == "charlie"
                || usuario == "sacha" && password == "sacha"
                || usuario == "javo" && password == "javo"
                || usuario == "heber" && password == "heber") {
                utils.goTo(
                    InicioActivity::class.java,
                    true,
                    "usuario" to usuario)
                utils.toast("Sesion iniciada...")
            }
            // Usuario o contraseña incorrectos
            else {
                utils.toast("Usuario o contraseña incorrectos")
            }
        }
    }
}