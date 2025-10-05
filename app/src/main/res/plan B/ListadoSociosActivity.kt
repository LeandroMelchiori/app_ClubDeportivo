package `plan B`

import kotlin.jvm.java

class ListadoSociosActivity : androidx.appcompat.app.AppCompatActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.clubdeportivo.R.layout.activity_listado_socios)

        val bottom =
            androidx.appcompat.app.AppCompatActivity.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                com.example.clubdeportivo.R.id.bottomNav
            )
        bottom.selectedItemId = com.example.clubdeportivo.R.id.nav_listas

        bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.clubdeportivo.R.id.nav_pagos -> {
                    android.app.Activity.startActivity(
                        android.content.Intent(
                            this,
                            com.example.clubdeportivo.PagosActivity::class.java
                        )
                    )
                    true
                }
                com.example.clubdeportivo.R.id.nav_activity -> {
                    android.app.Activity.startActivity(
                        android.content.Intent(
                            this,
                            com.example.clubdeportivo.ActividadesActivity::class.java
                        )
                    )
                    true
                }
                com.example.clubdeportivo.R.id.nav_home -> {
                    android.app.Activity.startActivity(
                        android.content.Intent(
                            this,
                            com.example.clubdeportivo.InicioActivity::class.java
                        )
                    )
                    true
                }
                else -> true
            }
        }
    }
}