package com.example.travelgo.DataBase.DAO

import androidx.room.*
import com.example.travelgo.DataBase.Entidades.LugarTuristico

@Dao
interface LugarTuristicoDAO{
    @Query("SELECT * FROM lugares")
    suspend fun obtenerTodos(): List<LugarTuristico>

    //@Query("SELECT * FROM lugares WHERE esFavorito = 1")
    //suspend fun obtenerFavoritos(): List<LugarTuristico>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLugar(lugar: LugarTuristico)

    @Update
    suspend fun actualizarLugar(lugar: LugarTuristico)

    @Delete
    suspend fun eliminarLugar(lugar: LugarTuristico)
}