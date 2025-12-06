package com.example.clubdeportivo

import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView


class ActividadesActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var tvBienvenida: TextView
    private lateinit var btnAgregar: MaterialButton
    private lateinit var bottom: BottomNavigationView
    private lateinit var etBuscar: SearchView
    private lateinit var db: DBHelper
    private lateinit var utils: AppUtils
    private lateinit var adapter: ActividadCardAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actividades)

        db = DBHelper(this)
        utils = AppUtils(this)

        // --- refs UI ---
        btnAgregar   = findViewById(R.id.btnAgregar)
        tvBienvenida = findViewById(R.id.tvBienvenida)
        bottom       = findViewById(R.id.bottomNav)
        etBuscar     = findViewById(R.id.etBuscar)
        rv           = findViewById(R.id.contenedorActividades)

        // Encabezado
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvFecha = findViewById<TextView>(R.id.tvFecha)
        tvBienvenida.text = "Bienvenido, $usuario"
        tvFecha.text = utils.fechaActualFormato()

        // Agregar horario
        btnAgregar.setOnClickListener {
            utils.goTo(
                NuevoHorarioActividadActivity::class.java,
                finishCurrent = false,
                "usuario" to usuario
            )
        }
        adapter = ActividadCardAdapter(
            onEditar = { act ->
                // Editar actividad
                utils.goTo(
                    EditarActividadActivity::class.java,
                    true,
                    "usuario" to usuario,
                    "dh_id" to act.idDiaHorario,
                    "id_actividad" to act.idActividad,
                    "nombre_act" to act.nombre,
                    "profesor" to act.profesor,
                    "dia" to act.dia,
                    "hora_inicio" to act.horaInicio,
                    "hora_fin" to act.horaFin,
                    "precio" to act.precio
                )
            },
            onEliminar = { act ->
                // Dialogo de confirmación
                utils.confirmDialog(
                    "Eliminar actividad",
                    "¿Eliminar \"${act.nombre}\" en el horario de las ${act.etiquetaHorario}?"
                ) {
                    val ok = db.darDeBajaHorario(act.idDiaHorario)
                    if (ok) {
                        utils.toast("Actividad eliminada")
                        // refrescá la lista respetando el filtro actual del SearchView
                        recargarLista(findViewById<SearchView>(R.id.etBuscar).query?.toString())
                    } else {
                        utils.toast("Error al eliminar")
                    }
                }
            }
        )
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(true)
        rv.adapter = adapter

        // Carga inicial
        recargarLista(null)

        // Buscador en vivo
        etBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtro = newText?.trim().orEmpty()
                recargarLista(filtro.ifEmpty { null })
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                val filtro = query?.trim().orEmpty()
                recargarLista(filtro.ifEmpty { null })
                return true
            }
        })

        // Bottom
        utils.setupBottomNav(
            bottom,
            usuario,
            R.id.nav_activity
        )
    }
    private fun recargarLista(filtro: String?) {
        val lista = if (filtro.isNullOrBlank())
            db.obtenerActividadesPorHorario()
        else
            db.buscarActividadesPorNombre(filtro)
        adapter.submitList(lista)
    }
}
