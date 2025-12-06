package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
class InicioActivity : AppCompatActivity() {
    private lateinit var utils: AppUtils
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        // Helpers
        utils = AppUtils(this)
        db = DBHelper(this)
        // Datos intents
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"

        // Encabezado
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        tvBienvenida.text = "Bienvenido, $usuario"
        tvFecha.text = utils.fechaActualFormato()

        // Carga actividades del dia
        val diaHoy = LocalDate.now().dayOfWeek.value % 7  // Lunes=1 ... Domingo=7 → ajustamos a 0–6
        val actividades = db.obtenerActividadesDelDia(diaHoy)
        renderActividadesHoy(actividades, usuario)

        // Boton nuevo usuario
        val btnUsuario = findViewById<MaterialButton>(R.id.btnUsuario)
        btnUsuario.setOnClickListener {
        utils.goTo(NuevoUsuarioActivity::class.java,
            finishCurrent = true,
            "usuario" to usuario)
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_home)
    }
    // Renderiza la lista de actividades del día con el item de tarjeta
    private fun renderActividadesHoy(actividades: List<ActividadHoy>, usuario: String) {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorActividadesHoy)
        contenedor.removeAllViews()

        val inflater = LayoutInflater.from(this)

        // Actividades del día
        actividades.forEach { act ->
            val view = inflater.inflate(R.layout.item_actividad_hoy, contenedor, false)
            val tvTitulo  = view.findViewById<TextView>(R.id.tvTitulo)
            val btnAccion = view.findViewById<ImageButton>(R.id.btnAccion)
            tvTitulo.text = "${act.horaInicio} - ${act.nombre}"

            // Boton para dirigir al formulario de pago de actividad
            btnAccion.setOnClickListener {
                intent = Intent(this, PagoActividadActivity::class.java)
                intent.putExtra("idActividad", act.id)
                intent.putExtra("nombreActividad", act.nombre)
                intent.putExtra("precioActividad", act.precio)
                intent.putExtra("diaActividad", act.dia)
                intent.putExtra("horaInicio", act.horaInicio)
                intent.putExtra("usuario", usuario)
                startActivity(intent)
            }
            contenedor.addView(view)
        }
    }
    // Modelo de datos para la tarjeta de actividad del día
    data class ActividadHoy(
        val id: Int,
        val nombre: String,
        val dia: Int,
        val horaInicio: String,
        val horaFin: String,
        val precio: Double
    )
}