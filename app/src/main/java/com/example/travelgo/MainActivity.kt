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
import com.example.travelgo.DataBase.Entidades.Rol
import com.example.travelgo.Auth.SessionManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LugarAdapter
    private lateinit var db: AppDatabase
    private val listaLugares = mutableListOf<LugarTuristico>()
    private var rolUsuario: Rol? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerViewLugares)
        recyclerView.layoutManager = LinearLayoutManager(this)

        db = AppDatabase.getInstance(applicationContext)

        // 🔹 Rol del usuario desde sesión
        rolUsuario = SessionManager(this).obtenerRol()

        adapter = LugarAdapter(
            lugares = listaLugares,
            rol = rolUsuario ?: Rol.CLIENTE,
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
                            adapter.eliminarConAnimacion(lugar)
                        }
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        )

        recyclerView.adapter = adapter

        // 🔹 FAB solo para admin
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarLugar)
        if (rolUsuario == Rol.ADMIN) {
            fabAgregar.show()
            fabAgregar.setOnClickListener {
                startActivity(Intent(this, AddLugarActivity::class.java))
            }
        } else {
            fabAgregar.hide()
        }

        lifecycleScope.launch { cargarLugaresDesdeBD() }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch { cargarLugaresDesdeBD() }
    }

    // 🔹 Menú del Toolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_filtros, menu)
        menuInflater.inflate(R.menu.menu_main, menu)

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
            R.id.action_refresh -> {
                lifecycleScope.launch { cargarLugaresDesdeBD() }
                Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.action_logout -> {
                SessionManager(this).cerrarSesion()
                val intent = Intent(this, LoginActivity::class.java) // 👈 cambio a InicioActivity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
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
        adapter.actualizarLista(lugares)
    }

    // 🔹 Diálogo selección de país
    private fun mostrarDialogoFiltroPais() {
        lifecycleScope.launch {
            val paises = withContext(Dispatchers.IO) {
                db.lugarTuristicoDao().obtenerPaises()
            }

            if (paises.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "No hay países disponibles en la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
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

    // 🔹 Diálogo selección de categoría
    private fun mostrarDialogoFiltroCategoria() {
        lifecycleScope.launch {
            val categorias = withContext(Dispatchers.IO) {
                db.lugarTuristicoDao().obtenerCategorias()
            }

            if (categorias.isEmpty()) {
                Toast.makeText(
                    this@MainActivity,
                    "No hay categorías disponibles en la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
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
