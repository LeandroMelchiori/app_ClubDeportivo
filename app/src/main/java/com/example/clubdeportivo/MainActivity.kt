package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Variables
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContraseña = findViewById<EditText>(R.id.etContraseña)
        val btnLogin = findViewById<MaterialButton>(R.id.btnLogin)

        // Logica inicio de sesion
        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val contraseña = etContraseña.text.toString()

            if(usuario.isEmpty() || contraseña.isEmpty()){
                Toast.makeText(this, "Por favor ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            }else if(usuario == "admin" && contraseña == "admin"
                || usuario == "charlie" && contraseña == "charlie"
                || usuario == "sacha" && contraseña == "sacha"
                || usuario == "javo" && contraseña == "javo"
                || usuario == "heber" && contraseña == "heber"){
                val intent = Intent(this, InicioActivity::class.java)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
            }else{
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
