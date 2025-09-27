package com.example.travelgo

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.travelgo.Adapters.UsuarioAdapter
import com.example.travelgo.Auth.SessionManager
import com.example.travelgo.DataBase.AppDatabase
import com.example.travelgo.DataBase.Entidades.Rol
import com.example.travelgo.DataBase.Entidades.Usuario
import com.example.travelgo.Seguridad.HashUtil
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GestionUsuariosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuarioAdapter
    private var usuarioLogueadoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_usuarios)

        // 🔹 Configurar Toolbar como ActionBar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarUsuarios)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestión de Usuarios"

        // 🔹 Acción del botón back
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        recyclerView = findViewById(R.id.recyclerUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val sessionManager = SessionManager(this)
        val usuarioLogueado = sessionManager.obtenerUsuario()
        usuarioLogueadoId = usuarioLogueado?.id ?: -1

        adapter = UsuarioAdapter(mutableListOf(), usuarioLogueadoId) { usuario ->
            mostrarDialogoAcciones(usuario)
        }
        recyclerView.adapter = adapter

        // 🔹 SwipeRefresh para recargar usuarios
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefreshUsuarios)
        swipeRefresh.setOnRefreshListener {
            cargarUsuarios()
            swipeRefresh.isRefreshing = false
        }

        cargarUsuarios()
    }

    // 🔹 Cargar usuarios desde la BD
    private fun cargarUsuarios() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            val listaUsuarios = withContext(Dispatchers.IO) {
                db.usuarioDao().obtenerTodos()
            }

            runOnUiThread {
                adapter.actualizarLista(listaUsuarios)
            }
        }
    }

    // 🔹 Menú superior
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_gestion_usuarios, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_agregar_usuario -> {
                mostrarDialogoAgregar()
                true
            }
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // 🔹 Diálogo de acciones sobre un usuario
    private fun mostrarDialogoAcciones(usuario: Usuario) {
        if (usuario.rol == Rol.ADMIN) {
            Toast.makeText(this, "El administrador no se puede editar ni eliminar", Toast.LENGTH_SHORT).show()
            return
        }

        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Acciones para ${usuario.nombre}")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> mostrarDialogoEditar(usuario)
                    1 -> eliminarUsuario(usuario)
                }
            }
            .show()
    }

    // 🔹 Editar usuario
    private fun mostrarDialogoEditar(usuario: Usuario) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val inputNombre = EditText(this).apply {
            hint = "Nombre"
            setText(usuario.nombre)
        }
        val inputEmail = EditText(this).apply {
            hint = "Email"
            setText(usuario.email)
        }

        layout.addView(inputNombre)
        layout.addView(inputEmail)

        AlertDialog.Builder(this)
            .setTitle("Editar Usuario")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = inputNombre.text.toString().trim()
                val nuevoEmail = inputEmail.text.toString().trim()

                if (nuevoNombre.isNotEmpty() && nuevoEmail.isNotEmpty()) {
                    val usuarioEditado = usuario.copy(nombre = nuevoNombre, email = nuevoEmail)
                    actualizarUsuario(usuarioEditado)
                } else {
                    Toast.makeText(this, "No puede haber campos vacíos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // 🔹 Agregar usuario
    // 🔹 Agregar usuario
    private fun mostrarDialogoAgregar() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 10)
        }

        val inputNombre = EditText(this).apply { hint = "Nombre" }
        val inputEmail = EditText(this).apply { hint = "Email" }
        val inputPassword = EditText(this).apply {
            hint = "Contraseña"
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        layout.addView(inputNombre)
        layout.addView(inputEmail)
        layout.addView(inputPassword)

        AlertDialog.Builder(this)
            .setTitle("Nuevo Usuario")
            .setView(layout)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = inputNombre.text.toString().trim()
                val email = inputEmail.text.toString().trim()
                val password = inputPassword.text.toString().trim()

                if (nombre.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val nuevoUsuario = Usuario(
                        id = 0,
                        nombre = nombre,
                        email = email,
                        passwordHash = HashUtil.hashPassword(password),
                        rol = Rol.CLIENTE
                    )
                    guardarUsuario(nuevoUsuario)
                } else {
                    Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarUsuario(usuario: Usuario) {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            withContext(Dispatchers.IO) {
                db.usuarioDao().insertarUsuario(usuario)
            }
            runOnUiThread {
                Toast.makeText(this@GestionUsuariosActivity, "Usuario agregado", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            }
        }
    }

    private fun actualizarUsuario(usuario: Usuario) {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            withContext(Dispatchers.IO) {
                db.usuarioDao().actualizarUsuario(usuario)
            }
            runOnUiThread {
                Toast.makeText(this@GestionUsuariosActivity, "Usuario actualizado", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            }
        }
    }

    private fun eliminarUsuario(usuario: Usuario) {
        val sessionManager = SessionManager(this)
        val usuarioLogueado = sessionManager.obtenerUsuario()

        if (usuarioLogueado != null && usuario.id == usuarioLogueado.id) {
            Toast.makeText(this, "No podés eliminar tu propio usuario", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            withContext(Dispatchers.IO) {
                db.usuarioDao().eliminarUsuario(usuario.id)
            }
            runOnUiThread {
                Toast.makeText(this@GestionUsuariosActivity, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                cargarUsuarios()
            }
        }
    }
}