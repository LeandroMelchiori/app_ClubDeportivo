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

// NoSocioAdapter.kt (usá PascalCase para el nombre del archivo y clase)
class NoSocioAdapter :
    ListAdapter<DBHelper.NoSocioCard, NoSocioAdapter.VH>(DIFF) {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvUltimoPago: TextView = view.findViewById(R.id.tvUltimoPago)
        val btnAccion: Button = view.findViewById(R.id.btnAccion)
        val btnVerMas: Button = view.findViewById(R.id.btnVerMas)
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
        h.tvEstado.text = "Inactivo"          // ajustá si tenés campo estado
        h.tvUltimoPago.text = ns.ultimaPago ?: "Ultima actividad: No registra actividad"

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

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DBHelper.NoSocioCard>() {
            override fun areItemsTheSame(a: DBHelper.NoSocioCard, b: DBHelper.NoSocioCard) =
                a.dni == b.dni
            override fun areContentsTheSame(a: DBHelper.NoSocioCard, b: DBHelper.NoSocioCard) =
                a == b
        }
    }
}
