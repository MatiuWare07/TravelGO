package com.example.travelgo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelgo.DataBase.Entidades.LugarTuristico
import com.example.travelgo.R

class LugarAdapter(
    private val lista: List<LugarTuristico>,
    private val onClick: (LugarTuristico) -> Unit
) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.txtNombreItem)
        val imagen: ImageView = itemView.findViewById(R.id.imgItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false)
        return LugarViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = lista[position]
        holder.nombre.text = lugar.nombre
        holder.imagen.setImageResource(lugar.imagenResId)

        holder.itemView.setOnClickListener {
            onClick(lugar)
        }
    }

    override fun getItemCount(): Int = lista.size
}
