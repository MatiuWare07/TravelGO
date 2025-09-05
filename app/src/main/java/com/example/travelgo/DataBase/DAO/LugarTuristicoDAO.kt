package com.example.travelgo.DataBase.DAO

import androidx.room.*
import com.example.travelgo.DataBase.Entidades.Categoria
import com.example.travelgo.DataBase.Entidades.LugarTuristico

@Dao
interface LugarTuristicoDAO {
    @Query("SELECT * FROM lugares")
    suspend fun obtenerTodos(): List<LugarTuristico>

    @Query("SELECT * FROM lugares WHERE categoria = :categoria")
    suspend fun obtenerPorCategoria(categoria: Categoria): List<LugarTuristico>

    @Query("SELECT * FROM lugares WHERE pais = :pais")
    suspend fun obtenerPorPais(pais: String): List<LugarTuristico>

    @Query("SELECT DISTINCT pais FROM lugares ORDER BY pais")
    suspend fun obtenerPaises(): List<String>

    @Query("SELECT DISTINCT categoria FROM lugares")
    suspend fun obtenerCategorias(): List<Categoria>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarLugar(lugar: LugarTuristico)

    @Update
    suspend fun actualizarLugar(lugar: LugarTuristico)

    @Delete
    suspend fun eliminarLugar(lugar: LugarTuristico)
}

