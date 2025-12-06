package com.example.clubdeportivo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ListadosActivity : AppCompatActivity() {
    private lateinit var db: DBHelper
    private lateinit var utils: AppUtils
    private lateinit var hoyISO: String
    private lateinit var rvNoSocios: RecyclerView
    private lateinit var rvSocios: RecyclerView
    private lateinit var rvVenc: RecyclerView
    private lateinit var tvNombreLista: TextView
    private lateinit var tvFecha: TextView
    private lateinit var noSocioAdapter: NoSocioAdapter
    private lateinit var socioAdapter: SocioAdapter
    private lateinit var vencimientoAdapter: VencimientoAdapter
    private lateinit var verMasLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listados)

        // Helpers
        db = DBHelper(this)
        utils = AppUtils(this)

        verMasLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                refreshVisibleList()
            }
        }

        // Views
        rvNoSocios = findViewById(R.id.rvNoSocios)
        rvSocios   = findViewById(R.id.rvSocios)
        rvVenc     = findViewById(R.id.rvVencimientos)
        tvNombreLista = findViewById(R.id.tvNombreLista)
        tvFecha = findViewById(R.id.tvFecha)

        // Fecha actual
        hoyISO = utils.hoyIso()
        val hoy = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

        // Encabezado
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        tvBienvenida.text = "Bienvenido, $usuario"
        tvFecha.text = utils.fechaActualFormato()

        // Layout Manager
        rvNoSocios.layoutManager = LinearLayoutManager(this)
        rvSocios.layoutManager   = LinearLayoutManager(this)
        rvVenc.layoutManager     = LinearLayoutManager(this)

        //  crear instancias
        noSocioAdapter = NoSocioAdapter()
        socioAdapter   = SocioAdapter()
        vencimientoAdapter    = VencimientoAdapter()

        // Asignar adapters
        rvNoSocios.adapter = noSocioAdapter
        rvSocios.adapter   = socioAdapter
        rvVenc.adapter     = vencimientoAdapter
        rvNoSocios.setHasFixedSize(true)
        rvSocios.setHasFixedSize(true)
        rvVenc.setHasFixedSize(true)

        // Listados
        renderNoSocios(db.obtenerNoSocios())
        renderSocios(db.obtenerSocios())
        renderVencimientos(db.obtenerVencimientos(hoy))

        // Botones listas
        val botonVencimiento: Button = findViewById(R.id.btnListVencimientos)
        val botonSocios: Button = findViewById(R.id.btnListSocios)
        val botonNoSocios: Button = findViewById(R.id.btnListNoSocios)

        // Lista por defecto
        botonNoSocios.setTextColor(Color.WHITE)
        botonSocios.setTextColor(Color.BLACK)
        botonVencimiento.setTextColor(Color.BLACK)
        tvNombreLista.text = "Listado No Socios"

        // Buscador
        val svBuscar = findViewById<SearchView>(R.id.svBuscar)
        svBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarSegunListaActual(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarSegunListaActual(newText.orEmpty())
                return true
            }
        })

        // onClick
        botonVencimiento.setOnClickListener {
            mostrar(rvVenc)
            botonNoSocios.setTextColor(Color.BLACK)
            botonSocios.setTextColor(Color.BLACK)
            botonVencimiento.setTextColor(Color.WHITE)
            tvNombreLista.text = "Listado Vencimientos"
        }
        botonSocios.setOnClickListener {
            mostrar(rvSocios)
            botonNoSocios.setTextColor(Color.BLACK)
            botonSocios.setTextColor(Color.WHITE)
            botonVencimiento.setTextColor(Color.BLACK)
            tvNombreLista.text = "Listado Socios"
        }
        botonNoSocios.setOnClickListener {
            mostrar(rvNoSocios)
            botonNoSocios.setTextColor(Color.WHITE)
            botonSocios.setTextColor(Color.BLACK)
            botonVencimiento.setTextColor(Color.BLACK)
            tvNombreLista.text = "Listado No Socios"
        }

        // Bottom
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        utils.setupBottomNav(bottom, usuario, R.id.nav_listas)
    }
    fun mostrar(rv: RecyclerView) {
        rvNoSocios.visibility = View.GONE
        rvSocios.visibility   = View.GONE
        rvVenc.visibility     = View.GONE
        rv.visibility         = View.VISIBLE
    }
    private fun renderNoSocios(lista: List<DBHelper.NoSocioCard>) = noSocioAdapter.submitList(lista)
    private fun renderSocios(lista: List<DBHelper.SocioCard>)     = socioAdapter.submitList(lista)
    private fun renderVencimientos(lista: List<DBHelper.VencimientoCard>) = vencimientoAdapter.submitList(lista)
    private fun refreshVisibleList() {
        val rvSocios        = findViewById<RecyclerView>(R.id.rvSocios)
        val rvNoSocios      = findViewById<RecyclerView>(R.id.rvNoSocios)
        val rvVencimientos  = findViewById<RecyclerView>(R.id.rvVencimientos)
        when {
            rvSocios.visibility == View.VISIBLE ->
                renderSocios(db.obtenerSocios())

            rvNoSocios.visibility == View.VISIBLE ->
                renderNoSocios(db.obtenerNoSocios())

            rvVencimientos.visibility == View.VISIBLE ->
                renderVencimientos(db.obtenerVencimientos(hoyISO))

            else ->
                renderNoSocios(db.obtenerNoSocios()) // fallback
        }
    }
    private fun filtrarSegunListaActual(texto: String) {
        when {
            rvNoSocios.visibility == View.VISIBLE ->
                noSocioAdapter.filtrarPorNombre(texto)

            rvSocios.visibility == View.VISIBLE ->
                socioAdapter.filtrarPorNombre(texto)

            rvVenc.visibility == View.VISIBLE ->
                vencimientoAdapter.filtrarPorNombre(texto) // si implementás filtro ahí
        }
    }
}
