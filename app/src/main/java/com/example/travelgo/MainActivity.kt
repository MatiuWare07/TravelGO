package com.example.travelgo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.travelgo.Adapter.LugarAdapter
import com.example.travelgo.DataBase.AppDatabase
import com.example.travelgo.DataBase.Entidades.Categoria
import com.example.travelgo.DataBase.Entidades.LugarTuristico
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LugarAdapter
    private lateinit var db: AppDatabase
    private val listaLugares = mutableListOf<LugarTuristico>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔹 Vincular el toolbar como ActionBar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerViewLugares)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getInstance(applicationContext)

        adapter = LugarAdapter(
            listaLugares,
            onItemClick = { lugar ->
                val intent = Intent(this, LugarDetalleActivity::class.java).apply {
                    putExtra("nombre", lugar.nombre)
                    putExtra("descripcion", lugar.descripcion)
                    putExtra("categoria", lugar.categoria)
                    putExtra("latitud", lugar.latitud)
                    putExtra("longitud", lugar.longitud)
                    putExtra("imagenResId", lugar.imagenResId ?: -1)
                    putExtra("imagenUri", lugar.imagenUri)
                }
                startActivity(intent) 
            },
            onDeleteClick = { lugar ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar lugar")
                    .setMessage("¿Seguro que querés eliminar \"${lugar.nombre}\"?")
                    .setPositiveButton("Sí") { _, _ ->
                        lifecycleScope.launch {
                            withContext(Dispatchers.IO) {
                                db.lugarTuristicoDao().eliminarLugar(lugar)
                            }
                            runOnUiThread {
                                adapter.eliminarConAnimacion(lugar)
                            }
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        recyclerView.adapter = adapter

        lifecycleScope.launch {
            cargarLugaresDesdeBD()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { cargarLugaresDesdeBD() }
    }

    // 🔹 Menú de filtros
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filtros, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView

        searchView?.queryHint = "Buscar lugares..."

        searchView?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filtro_todos -> {
                lifecycleScope.launch { cargarLugaresDesdeBD() }
            }
            R.id.filtro_categoria -> {
                mostrarDialogoFiltroCategoria()
            }
            R.id.filtro_pais -> {
                mostrarDialogoFiltroPais()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 🔹 Cargar datos
    private suspend fun cargarLugaresDesdeBD() {
        val lugares = withContext(Dispatchers.IO) {
            db.lugarTuristicoDao().obtenerTodos()
        }
        actualizarLista(lugares)
    }

    private suspend fun cargarLugaresPorCategoria(categoria: Categoria) {
        val lugares = withContext(Dispatchers.IO) {
            db.lugarTuristicoDao().obtenerPorCategoria(categoria)
        }
        actualizarLista(lugares)
    }

    private suspend fun cargarLugaresPorPais(pais: String) {
        val lugares = withContext(Dispatchers.IO) {
            db.lugarTuristicoDao().obtenerPorPais(pais)
        }
        actualizarLista(lugares)
    }

    private fun actualizarLista(lugares: List<LugarTuristico>) {
        runOnUiThread {
            adapter.actualizarLista(lugares)
        }
    }

    // 🔹 Diálogo selección de país
    private fun mostrarDialogoFiltroPais() {
        lifecycleScope.launch {
            val paises = withContext(Dispatchers.IO) {
                db.lugarTuristicoDao().obtenerPaises()
            }

            if (paises.isEmpty()) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "No hay países disponibles en la base de datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                runOnUiThread {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Seleccioná un país")
                        .setItems(paises.toTypedArray()) { _, which ->
                            val paisSeleccionado = paises[which]
                            lifecycleScope.launch { cargarLugaresPorPais(paisSeleccionado) }
                        }
                        .show()
                }
            }
        }
    }

    // 🔹 Diálogo selección de categoría
    private fun mostrarDialogoFiltroCategoria() {
        lifecycleScope.launch {
            val categorias = withContext(Dispatchers.IO) {
                db.lugarTuristicoDao().obtenerCategorias()
            }

            if (categorias.isEmpty()) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "No hay categorías disponibles en la base de datos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                runOnUiThread {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Seleccioná una categoría")
                        .setItems(categorias.map { it.name }.toTypedArray()) { _, which ->
                            val categoriaSeleccionada = categorias[which]
                            lifecycleScope.launch { cargarLugaresPorCategoria(categoriaSeleccionada) }
                        }
                        .show()
                }
            }
        }
    }
}
