package com.example.travelgo.DataBase.Entidades

import android.app.role.RoleManager
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val email: String,
    val passwordHash: String,
    val rol: Rol
)

enum class Rol{
    CLIENTE,
    ADMIN
}
