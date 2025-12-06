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

class SocioAdapter(
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
        val vEstado: View = view.findViewById(R.id.vEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = getItem(position)
        h.tvNombre.text = "${item.nombre} ${item.apellido}"
        if (ChronoUnit.DAYS.between(LocalDate.parse(item.ultimoPago), LocalDate.now()) < 45){
            h.tvEstado.text = "Socio activo"
            h.vEstado.setBackgroundResource(R.drawable.bg_pill_green)
        } else{
            h.tvEstado.text ="Socio inactivo"
            h.vEstado.setBackgroundResource(R.drawable.bg_pill_red)
        }
        h.tvUltimoPago.text = "Últ. pago: ${item.ultimoPago}"

        // textos de botones (si querés otros, cambialos)
        h.btnAccion.text = "Pagar  cuota"
        h.btnVerMas.text = "Ver más"

        h.btnAccion.setOnClickListener { val c = h.itemView.context
            c.startActivity(Intent(c, PagoDeCuotaActivity::class.java).apply {
                putExtra("dni", item.dni)
                putExtra("nombre", "${item.apellido}, ${item.nombre}")
                putExtra("tipoOperacion", "Cuota mensual")
                putExtra("ultimoPago", item.ultimoPago)
                putExtra("precio", "30000")
                putExtra("esSocio", true)
            })
        }
        h.btnVerMas.setOnClickListener { val c = h.itemView.context
            c.startActivity(Intent(c, VerMasActivity::class.java).putExtra("dni", item.dni)) }
    }

    private var fullList: List<DBHelper.SocioCard> = emptyList()

    override fun submitList(list: List<DBHelper.SocioCard>?) {
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

}
