package com.example.clubdeportivo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppUtils(private val ctx: Context) {

    fun toast(msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
    }

    fun <T> goTo(
        destination: Class<T>,
        usuario: String? = null,
        finishCurrent: Boolean = false
    ) {
        val intent = Intent(ctx, destination)
        if (usuario != null) intent.putExtra("usuario", usuario)
        ctx.startActivity(intent)
        if (finishCurrent && ctx is Activity) ctx.finish()
    }

    fun diaTexto(diaActividad: Int): String {
        val dias = arrayOf("Domingo","Lunes","Martes","Miércoles","Jueves","Viernes","Sábado")
        return when (diaActividad) {
            in 0..6 -> dias[diaActividad]
            7       -> dias[0]
            else    -> diaActividad.toString()
        }
    }
    fun fechaActualFormato(): String {
        val formato = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "AR"))
        val fechaHoy = formato.format(Date())
        return fechaHoy.replaceFirstChar { it.uppercase() }
    }
    fun confirmDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(ctx)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Sí") { _, _ -> onConfirm() }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    fun getSelectedRadioText(activity: Activity, group: RadioGroup): String? {
        val id = group.checkedRadioButtonId
        if (id == -1) return null
        return activity.findViewById<RadioButton>(id).text.toString()
    }
    fun setupBottomNav(
        bottom: BottomNavigationView,
        usuario: String,
        currentItemId: Int
    ) {
        // Marcar el item actual
        bottom.selectedItemId = currentItemId

        bottom.setOnItemSelectedListener { item ->
            // 1) Si toca el mismo item en el que ya está, no hacemos nada
            if (item.itemId == currentItemId) {
                return@setOnItemSelectedListener true
            }

            // 2) Según el item, navegamos a la Activity correspondiente
            when (item.itemId) {
                R.id.nav_home -> {
                    goTo(InicioActivity::class.java, usuario, finishCurrent = true)
                }
                R.id.nav_pagos -> {
                    goTo(ResumenMensualActivity::class.java, usuario, finishCurrent = true)
                }
                R.id.nav_activity -> {
                    goTo(ActividadesActivity::class.java, usuario, finishCurrent = true)
                }
                R.id.nav_settings -> {
                    goTo(ConfiguracionActivity::class.java, usuario, finishCurrent = true)
                }
                R.id.nav_listas -> {
                    goTo(ListadosActivity::class.java, usuario, finishCurrent = true)
                }
            }

            true
        }
    }


}
