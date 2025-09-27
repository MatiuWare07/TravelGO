package com.example.travelgo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.travelgo.DataBase.Entidades.LugarTuristico
import com.example.travelgo.DataBase.Entidades.Rol
import com.example.travelgo.R
import java.io.File
import android.widget.Filter
import android.widget.Filterable
import java.util.*

class LugarAdapter(
    private val lugares: MutableList<LugarTuristico>,
    private val rol: Rol,
    private val onItemClick: (LugarTuristico) -> Unit,
    private val onDeleteClick: (LugarTuristico) -> Unit
) : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>(), Filterable {

    private var lugaresFiltrados: MutableList<LugarTuristico> = ArrayList(lugares)

    inner class LugarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNombreItem: TextView = itemView.findViewById(R.id.txtNombreItem)
        val txtDescripcionItem: TextView = itemView.findViewById(R.id.txtDescripcionItem)
        val imgItem: ImageView = itemView.findViewById(R.id.imgItem)
        val btnEliminar: ImageView = itemView.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lugar, parent, false)
        return LugarViewHolder(view)
    }

    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = lugaresFiltrados[position]

        holder.txtNombreItem.text = lugar.nombre
        holder.txtDescripcionItem.text = lugar.descripcion ?: ""

        // 🔹 Cargar imagen
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

        // 🔹 Acción al hacer click en el item → abre el detalle
        holder.itemView.setOnClickListener { onItemClick(lugar) }

        // 🔹 Mostrar / ocultar botón según rol
        if (rol == Rol.ADMIN) {
            holder.btnEliminar.visibility = View.VISIBLE
            holder.btnEliminar.setOnClickListener { onDeleteClick(lugar) }
        } else {
            holder.btnEliminar.visibility = View.GONE
        }
    }

    override fun getItemCount() = lugaresFiltrados.size

    fun actualizarLista(nuevosLugares: List<LugarTuristico>) {
        lugares.clear()
        lugares.addAll(nuevosLugares)
        lugaresFiltrados = ArrayList(lugares)
        notifyDataSetChanged()
    }

    fun eliminarConAnimacion(lugar: LugarTuristico) {
        val indexFiltrado = lugaresFiltrados.indexOf(lugar)
        if (indexFiltrado != -1) {
            lugaresFiltrados.removeAt(indexFiltrado)
            notifyItemRemoved(indexFiltrado)
        }
        val indexOriginal = lugares.indexOf(lugar)
        if (indexOriginal != -1) {
            lugares.removeAt(indexOriginal)
        }
    }

    // -------------------
    // 🔍 Filtro de búsqueda
    // -------------------
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(query: CharSequence?): FilterResults {
                val filtro = query?.toString()?.lowercase(Locale.ROOT)?.trim() ?: ""
                val resultados = if (filtro.isEmpty()) {
                    lugares
                } else {
                    lugares.filter {
                        it.nombre.lowercase(Locale.ROOT).contains(filtro) ||
                                (it.descripcion?.lowercase(Locale.ROOT)?.contains(filtro) ?: false) ||
                                it.pais.lowercase(Locale.ROOT).contains(filtro)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = resultados
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                lugaresFiltrados = (results?.values as? List<LugarTuristico>)?.toMutableList() ?: mutableListOf()
                notifyDataSetChanged()
            }
        }
    }
}
