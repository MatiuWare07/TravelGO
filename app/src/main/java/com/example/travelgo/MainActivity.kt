package com.example.travelgo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.travelgo.Adapter.LugarAdapter
import com.example.travelgo.DataBase.AppDatabase
import com.example.travelgo.DataBase.Entidades.LugarTuristico
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LugarAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewLugares)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "lugares-db"
        )
            .fallbackToDestructiveMigration() // 🧨 Importante para resetear la BD
            .build()

        lifecycleScope.launch {
            inicializarDatosSiVacio()
            cargarLugaresDesdeBD()
        }
    }

    private suspend fun inicializarDatosSiVacio() {
        val dao = db.lugarTuristicoDao()
        val lugaresExistentes = dao.obtenerTodos()
        if (lugaresExistentes.isEmpty()) {
            Log.d("MainActivity", "BD vacía. Insertando ejemplos...")

            dao.insertarLugar(
                LugarTuristico(
                    nombre = "Parque del Retiro",
                    descripcion = "Un hermoso parque en el centro de Madrid",
                    categoria = "Parque",
                    latitud = 40.4154,
                    longitud = -3.6843,
                    imagenResId = R.drawable.parque_retiro
                )
            )

            dao.insertarLugar(
                LugarTuristico(
                    nombre = "Museo del Prado",
                    descripcion = "Uno de los museos más importantes del mundo",
                    categoria = "Museo",
                    latitud = 40.4138,
                    longitud = -3.6921,
                    imagenResId = R.drawable.prado
                )
            )

            Log.d("MainActivity", "Datos de ejemplo insertados correctamente.")
        } else {
            Log.d("MainActivity", "Ya hay datos en la base. No se insertan duplicados.")
        }
    }

    private suspend fun cargarLugaresDesdeBD() {
        val lugares = withContext(Dispatchers.IO) {
            db.lugarTuristicoDao().obtenerTodos()
        }

        Log.d("MainActivity", "Lugares cargados: ${lugares.size}")

        runOnUiThread {
            adapter = LugarAdapter(lugares) { lugar ->
                val intent = Intent(this, LugarDetalleActivity::class.java).apply {
                    putExtra("nombre", lugar.nombre)
                    putExtra("descripcion", lugar.descripcion)
                    putExtra("categoria", lugar.categoria)
                    putExtra("latitud", lugar.latitud)
                    putExtra("longitud", lugar.longitud)
                    putExtra("imagenResId", lugar.imagenResId)
                }
                startActivity(intent)
            }
            recyclerView.adapter = adapter
        }
    }
}

