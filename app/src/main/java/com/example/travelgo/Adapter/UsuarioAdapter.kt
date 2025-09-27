package com.example.travelgo.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.travelgo.DataBase.Entidades.Usuario
import com.example.travelgo.R

class UsuarioAdapter(
    private val usuarios: MutableList<Usuario>, // 👈 mutable
    private val usuarioLogueadoId: Int,         // 👈 lo pasamos desde la Activity
    private val onEliminarClick: (Usuario) -> Unit
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvEmail: TextView = view.findViewById(R.id.tvEmail)
        val tvRol: TextView = view.findViewById(R.id.tvRol)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.tvNombre.text = usuario.nombre
        holder.tvEmail.text = usuario.email
        holder.tvRol.text = usuario.rol.name

        if (usuario.id == usuarioLogueadoId) {
            holder.btnEliminar.isEnabled = false
            holder.btnEliminar.alpha = 0.5f
        } else {
            holder.btnEliminar.isEnabled = true
            holder.btnEliminar.alpha = 1f
            holder.btnEliminar.setOnClickListener {
                onEliminarClick(usuario)
            }
        }
    }

    override fun getItemCount(): Int = usuarios.size

    fun actualizarLista(nuevaLista: List<Usuario>) {
        usuarios.clear()
        usuarios.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}

