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
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class InicioActivity : AppCompatActivity() {
    private lateinit var utils: AppUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        // Helpers
        utils = AppUtils(this)

        // Dia de la semana
        val db = DBHelper(this)
        val diaHoy = LocalDate.now().dayOfWeek.value % 7  // Lunes=1 ... Domingo=7 → ajustamos a 0–6

        val actividades = db.obtenerActividadesDelDia(diaHoy) // devuelve List<ActividadHoy>

        // Recupera el nombre de usuario del intent y lo muestra
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"

        // Fecha encabezado
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        val formato = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "AR"))
        val fechaHoy = formato.format(Date())
        tvFecha.text = fechaHoy.replaceFirstChar { it.uppercase() }

        // Renderiza la lista de actividades del día
        renderActividadesHoy(actividades, usuario)

        // Boton nuevo usuario
        val btnUsuario = findViewById<MaterialButton>(R.id.btnUsuario)
        btnUsuario.setOnClickListener {
        val intent = Intent(this, NuevoUsuarioActivity::class.java)
        intent.putExtra("usuario", usuario)
        startActivity(intent)
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
    data class ActividadHoy(
        val id: Int,
        val nombre: String,
        val dia: Int,
        val horaInicio: String,
        val horaFin: String,
        val precio: Double
    )
}