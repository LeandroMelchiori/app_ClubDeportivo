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

class VencimientoAdapter(
    private val onAccion: (DBHelper.VencimientoCard) -> Unit = {},
    private val onVerMas: (DBHelper.VencimientoCard) -> Unit = {},
    // Podés usar un layout propio (p.ej. R.layout.item_vencimiento).
    // Mientras tanto reutilizo el de no socio que ya tenés.
    private val layoutRes: Int = R.layout.item_nosocio
) : ListAdapter<DBHelper.VencimientoCard, VencimientoAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DBHelper.VencimientoCard>() {
            override fun areItemsTheSame(
                oldItem: DBHelper.VencimientoCard,
                newItem: DBHelper.VencimientoCard
            ) = oldItem.dni == newItem.dni

            override fun areContentsTheSame(
                oldItem: DBHelper.VencimientoCard,
                newItem: DBHelper.VencimientoCard
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
        h.tvEstado.text = "Vence"
        h.tvNombre.text = "${item.nombre} ${item.apellido}"
        h.tvUltimoPago.text = "Venc.: ${item.fechaVenc}"

        h.btnAccion.text = "Pagar"
        h.btnVerMas.text = "Ver más"

        h.btnAccion.setOnClickListener { onAccion(item) }
        h.btnVerMas.setOnClickListener { val c = h.itemView.context
            c.startActivity(Intent(c, VerMasActivity::class.java).putExtra("dni", item.dni)) }
    }
}
