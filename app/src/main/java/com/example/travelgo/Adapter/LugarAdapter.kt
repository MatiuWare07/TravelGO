package com.example.travelgo.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelgo.DataBase.Entidades.LugarTuristico
import com.example.travelgo.R
import java.io.File

class LugarAdapter(
    private val lugares: List<LugarTuristico>,
    private val onItemClick: (LugarTuristico) -> Unit
) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    inner class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreItem: TextView = itemView.findViewById(R.id.txtNombreItem)
        val txtDescripcionItem: TextView = itemView.findViewById(R.id.txtDescripcionItem)
        val imgItem: ImageView = itemView.findViewById(R.id.imgItem)
        val btnVerEnMapa: Button = itemView.findViewById(R.id.btnVerEnMapa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false)
        return LugarViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = lugares[position]

        holder.txtNombreItem.text = lugar.nombre
        holder.txtDescripcionItem.text = lugar.descripcion ?: ""

        // Glide: prioridad a imagenUri (galería), si no imagenResId (ejemplos)
        if (!lugar.imagenUri.isNullOrEmpty()) {
            val file = File(lugar.imagenUri!!)
            Glide.with(holder.itemView.context)
                .load(file)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imgItem)
        } else if (lugar.imagenResId != null) {
            Glide.with(holder.itemView.context)
                .load(lugar.imagenResId)
                .placeholder(R.drawable.placeholder)
                .into(holder.imgItem)
        } else {
            holder.imgItem.setImageResource(R.drawable.placeholder)
        }

        holder.itemView.setOnClickListener { onItemClick(lugar) }

        holder.btnVerEnMapa.setOnClickListener {
            val uri = android.net.Uri.parse("geo:${lugar.latitud},${lugar.longitud}?q=${android.net.Uri.encode(lugar.nombre)}")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.setPackage("com.google.android.apps.maps")
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = lugares.size
}







