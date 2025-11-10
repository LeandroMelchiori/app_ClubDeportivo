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

class SocioAdapter(
    private val onAccion: (DBHelper.SocioCard) -> Unit = {},
    private val onVerMas: (DBHelper.SocioCard) -> Unit = {},
    // Si ya tenés un layout específico para socios, cambialo aquí
    private val layoutRes: Int = R.layout.item_nosocio
) : ListAdapter<DBHelper.SocioCard, SocioAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DBHelper.SocioCard>() {
            override fun areItemsTheSame(
                oldItem: DBHelper.SocioCard,
                newItem: DBHelper.SocioCard
            ) = oldItem.dni == newItem.dni

            override fun areContentsTheSame(
                oldItem: DBHelper.SocioCard,
                newItem: DBHelper.SocioCard
            ) = oldItem == newItem
        }
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvUltimoPago: TextView = view.findViewById(R.id.tvUltimoPago)
        val btnAccion: Button = view.findViewById(R.id.btnAccion)
        val btnVerMas: Button = view.findViewById(R.id.btnVerMas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = getItem(position)
        h.tvEstado.text = "Socio"
        h.tvNombre.text = "${item.nombre} ${item.apellido}"
        h.tvUltimoPago.text = "Últ. pago: ${item.ultimoPago}"

        // textos de botones (si querés otros, cambialos)
        h.btnAccion.text = "Acción"
        h.btnVerMas.text = "Ver más"

        h.btnAccion.setOnClickListener { onAccion(item) }
        h.btnVerMas.setOnClickListener { val c = h.itemView.context
            c.startActivity(Intent(c, VerMasActivity::class.java).putExtra("dni", item.dni)) }
    }
}
