package com.example.travelgo.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelgo.DataBase.DAO.LugarTuristicoDAO
import com.example.travelgo.DataBase.DAO.UsuarioDao
import com.example.travelgo.DataBase.Entidades.LugarTuristico
import com.example.travelgo.DataBase.Entidades.Rol
import com.example.travelgo.DataBase.Entidades.Usuario
import com.example.travelgo.Seguridad.HashUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [LugarTuristico::class, Usuario::class],
    version = 11,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lugarTuristicoDao(): LugarTuristicoDAO
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // También en onCreate (por si se borra la app)
                            insertarAdminPorDefecto()
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Cada vez que abre la DB, chequea si existe admin
                            insertarAdminPorDefecto()
                        }

                        private fun insertarAdminPorDefecto() {
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { instancia ->
                                    val dao = instancia.usuarioDao()
                                    val adminExistente = dao.buscarPorEmail("admin@travelgo.com")
                                    if (adminExistente == null) {
                                        val admin = Usuario(
                                            nombre = "Administrador",
                                            email = "admin@travelgo.com",
                                            passwordHash = HashUtil.hashPassword("admin123"),
                                            rol = Rol.ADMIN
                                        )
                                        dao.insertarUsuario(admin)
                                    }
                                }
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }
        }
    }
}

