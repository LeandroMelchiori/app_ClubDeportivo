package com.example.clubdeportivo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
class AppUtils(private val ctx: Context) {

    fun toast(msg: String) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
    }
    fun <T> goTo(
        destination: Class<T>,
        finishCurrent: Boolean = false,
        vararg extras: Pair<String, Any?>
    ) {
        val intent = Intent(ctx, destination)

        // Extras dinámicos opcionales
        extras.forEach { (key, value) ->
            when (value) {
                null -> intent.putExtra(key, null as String?)
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Long -> intent.putExtra(key, value)
                is Serializable -> intent.putExtra(key, value)
                is Parcelable -> intent.putExtra(key, value)
                else -> throw IllegalArgumentException("Tipo no soportado en goTo(): $key")
            }
        }

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
                    goTo(InicioActivity::class.java, finishCurrent = true,"usuario" to usuario)
                }
                R.id.nav_pagos -> {
                    goTo(ResumenMensualActivity::class.java, finishCurrent = true,"usuario" to usuario)
                }
                R.id.nav_activity -> {
                    goTo(ActividadesActivity::class.java, finishCurrent = true,"usuario" to usuario)
                }
                R.id.nav_settings -> {
                    goTo(ConfiguracionActivity::class.java, finishCurrent = true,"usuario" to usuario)
                }
                R.id.nav_listas -> {
                    goTo(ListadosActivity::class.java, finishCurrent = true,"usuario" to usuario)
                }
            }

            true
        }
    }
    fun normalizarFecha(input: String): String? {
        if (input.isBlank()) return null
        return try {
            val inFmt  = SimpleDateFormat("dd/MM/yyyy", Locale("es", "AR"))
            val outFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            outFmt.format(inFmt.parse(input)!!)
        } catch (_: Exception) {
            null
        }
    }
    fun hoyIso(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
    fun esDniValido(dni: String): Boolean =
        dni.matches(Regex("^\\d{8,9}$"))
    fun esTelefonoValido(tel: String): Boolean =
        tel.matches(Regex("^\\d{9,12}$"))
    fun esEmailValido(mail: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()
}
