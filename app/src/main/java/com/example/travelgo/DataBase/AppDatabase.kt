package com.example.travelgo.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelgo.DataBase.DAO.LugarTuristicoDAO
import com.example.travelgo.DataBase.Entidades.LugarTuristico

@Database(entities = [LugarTuristico::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lugarTuristicoDao(): LugarTuristicoDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}



