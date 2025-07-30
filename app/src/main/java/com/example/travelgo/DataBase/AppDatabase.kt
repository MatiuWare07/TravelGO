package com.example.travelgo.DataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.travelgo.DataBase.DAO.LugarTuristicoDAO
import com.example.travelgo.DataBase.Entidades.LugarTuristico

@Database(entities = [LugarTuristico::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lugarTuristicoDao(): LugarTuristicoDAO
}


