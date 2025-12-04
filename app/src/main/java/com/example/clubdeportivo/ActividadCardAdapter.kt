package com.example.clubdeportivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ActividadCardAdapter(
    private val onEditar: (DBHelper.ActividadCard) -> Unit,
    private val onEliminar: (DBHelper.ActividadCard) -> Unit
) : ListAdapter<DBHelper.ActividadCard, ActividadCardAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<DBHelper.ActividadCard>() {
            override fun areItemsTheSame(a: DBHelper.ActividadCard, b: DBHelper.ActividadCard) =
                a.idDiaHorario == b.idDiaHorario  // ← clave real del ítem

            override fun areContentsTheSame(a: DBHelper.ActividadCard, b: DBHelper.ActividadCard) =
                a.nombre == b.nombre &&
                        a.precio == b.precio &&
                        a.profesor == b.profesor &&
                        a.dia == b.dia &&
                        a.horaInicio == b.horaInicio &&
                        a.horaFin == b.horaFin
            // (si preferís, podés usar simplemente a == b porque es data class)
        }
    }

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvNombre: TextView = v.findViewById(R.id.tvNombre)
        val tvProfesores: TextView = v.findViewById(R.id.tvProfesor)
        val tvHorarios: TextView = v.findViewById(R.id.tvHorarios)
        val tvPrecio: TextView = v.findViewById(R.id.tvPrecio)
        val btnEditar: Button = v.findViewById(R.id.btnEditar)
        val btnEliminar: Button = v.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_actividades, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(h: VH, position: Int) {
        val item = getItem(position)
        h.tvNombre.text = item.nombre
        h.tvProfesores.text = item.profesor ?: "—"
        h.tvHorarios.text = item.etiquetaHorario ?: "—"
        h.tvPrecio.text = "Precio: $${"%.2f".format(item.precio)}"
        h.btnEditar.setOnClickListener { onEditar(item) }
        h.btnEliminar.setOnClickListener { onEliminar(item) }
    }
}