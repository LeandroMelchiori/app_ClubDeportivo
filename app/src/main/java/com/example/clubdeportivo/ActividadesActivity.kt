package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton   // si usás MaterialButton


class ActividadesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        findViewById<MaterialButton>(R.id.btnAgregar).setOnClickListener {
            startActivity(Intent(this, IngresarActividadActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnEditar).setOnClickListener {
            startActivity(Intent(this, EditarActividadActivity::class.java))
        }
    }
}
