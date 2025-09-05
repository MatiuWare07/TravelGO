package com.example.travelgo.DataBase.Entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lugares")
data class LugarTuristico(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val categoria: Categoria,
    val pais: String,
    val latitud: Double,
    val longitud: Double,
    val imagenResId: Int? = null,
    val imagenUri: String? = null
)









