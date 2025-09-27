package com.example.travelgo.Auth

import android.content.Context
import com.example.travelgo.DataBase.Entidades.Rol
import com.example.travelgo.DataBase.Entidades.Usuario

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun guardarUsuario(usuario: Usuario) {
        prefs.edit().apply {
            putInt("id", usuario.id)
            putString("nombre", usuario.nombre)
            putString("email", usuario.email)
            putString("rol", usuario.rol.name)
            apply()
        }
    }

    fun obtenerRol(): Rol? {
        val rolString = prefs.getString("rol", null)
        return rolString?.let { Rol.valueOf(it) }
    }

    fun obtenerUsuario(): Usuario? {
        val id = prefs.getInt("id", -1)
        val nombre = prefs.getString("nombre", null)
        val email = prefs.getString("email", null)
        val rolString = prefs.getString("rol", null)

        if (id == -1 || nombre == null || email == null || rolString == null) return null

        return Usuario(
            id = id,
            nombre = nombre,
            email = email,
            passwordHash = "", // no lo guardamos por seguridad
            rol = Rol.valueOf(rolString)
        )
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}

