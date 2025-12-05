package com.example.clubdeportivo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class NoSocioAdapter :
    ListAdapter<DBHelper.NoSocioCard, NoSocioAdapter.VH>(DIFF) {
    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvUltimoPago: TextView = view.findViewById(R.id.tvUltimoPago)
        val btnAccion: Button = view.findViewById(R.id.btnAccion)
        val btnVerMas: Button = view.findViewById(R.id.btnVerMas)
        val vEstado: View = view.findViewById(R.id.vEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nosocio, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val ns = getItem(pos)
        h.tvNombre.text = "${ns.apellido}, ${ns.nombre}"
        h.tvDni.text = "#${ns.dni}"
        if (
            !ns.ultimaPago.isNullOrBlank()
            && ChronoUnit.DAYS.between(LocalDate.parse(ns.ultimaPago), LocalDate.now()) < 30){
            h.tvEstado.text = "Activo"
            h.vEstado.setBackgroundResource(R.drawable.bg_pill_green)
        } else{
            h.tvEstado.text ="Inactivo"
            h.vEstado.setBackgroundResource(R.drawable.bg_pill_light)
        }
        h.tvUltimoPago.text = if (ns.ultimaPago != null) "Ultima actividad: ${ns.nombreAct} - ${ns.ultimaPago}" else "No registra actividad"

        // Botones
        h.btnAccion.setOnClickListener {
            val c = h.itemView.context
            c.startActivity(Intent(c, PagoDeCuotaActivity::class.java).apply {
                putExtra("dni", ns.dni)
                putExtra("nombre", "${ns.apellido}, ${ns.nombre}")
                putExtra("tipoOperacion", "Ser socio")
                putExtra("precio", "30000")
            })
        }
        h.btnVerMas.setOnClickListener {
            val c = h.itemView.context
            c.startActivity(Intent(c, VerMasActivity::class.java).putExtra("dni", ns.dni))
        }
    }
    private var fullList: List<DBHelper.NoSocioCard> = emptyList()
    override fun submitList(list: List<DBHelper.NoSocioCard>?) {
        fullList = list ?: emptyList()
        super.submitList(list)
    }
    fun filtrarPorNombre(texto: String) {
        val q = texto.trim().lowercase()

        val filtrada = if (q.isEmpty()) {
            fullList
        } else {
            fullList.filter {
                it.nombre.lowercase().contains(q) ||
                        it.apellido.lowercase().contains(q)
            }
        }

        super.submitList(filtrada)
    }
    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DBHelper.NoSocioCard>() {
            override fun areItemsTheSame(a: DBHelper.NoSocioCard, b: DBHelper.NoSocioCard) =
                a.dni == b.dni
            override fun areContentsTheSame(a: DBHelper.NoSocioCard, b: DBHelper.NoSocioCard) =
                a == b
        }
    }
}
