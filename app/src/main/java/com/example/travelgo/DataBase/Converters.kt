package com.example.travelgo.DataBase

import androidx.room.TypeConverter
import com.example.travelgo.DataBase.Entidades.Categoria

class Converters {
    @TypeConverter
    fun fromCategoria(categoria: Categoria): String = categoria.name

    @TypeConverter
    fun toCategoria(value: String): Categoria = Categoria.valueOf(value)
}
