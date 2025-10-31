package com.example.clubdeportivo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class DatoAdapter(
    private val datos: MutableList<String>,
    private val dbHelper: DBHelper
) : RecyclerView.Adapter<DatoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvItem: TextView = view.findViewById(R.id.tvItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dato, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val nombre = datos[position]
        holder.tvItem.text = nombre
        holder.itemView.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                .setPositiveButton("Si") { _, _ ->
                    dbHelper.eliminarProducto(nombre)
                    datos.removeAt(position)
                    notifyItemRemoved(position)
                }
                .setNegativeButton("No", null)
                .show()
            true
        }
    }

    override fun getItemCount() = datos.size
}