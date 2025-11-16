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

        // Calcular diferencia de días entre hoy y la fecha de vencimiento
        val hoy = LocalDate.now()
        val fv = LocalDate.parse(item.fechaVenc)   // viene en formato "yyyy-MM-dd"
        val diff = ChronoUnit.DAYS.between(fv, hoy)  // fv -> hoy
        val estadoTexto = when {
            diff == 0L -> "Vence hoy"
            diff > 0L  -> "Debe hace $diff días"
            else       -> "Vence en ${-diff} días"   // implementacion a futuro
        }

        // Rellenar la vista
        h.tvEstado.text = estadoTexto
        h.tvNombre.text = "${item.nombre} ${item.apellido}"
        h.tvUltimoPago.text = "Venc.: ${item.fechaVenc}"
        h.btnAccion.text = "Pagar"
        h.btnVerMas.text = "Ver más"

        // Botones
        h.btnAccion.setOnClickListener { val c = h.itemView.context
            c.startActivity(Intent(c, PagoDeCuotaActivity::class.java).apply {
                putExtra("dni", item.dni)
                putExtra("nombre", "${item.apellido}, ${item.nombre}")
                putExtra("tipoOperacion", "Cuota mensual - 10% Recargo")
                putExtra("ultimoPago", item.ultimoPago)
                putExtra("precio", "40000")
                putExtra("esSocio", true)
            })
        }
        h.btnVerMas.setOnClickListener { val c = h.itemView.context
            c.startActivity(Intent(c, VerMasActivity::class.java).putExtra("dni", item.dni)) }
    }

    private var fullList: List<DBHelper.VencimientoCard> = emptyList()
    override fun submitList(list: List<DBHelper.VencimientoCard>?) {
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
