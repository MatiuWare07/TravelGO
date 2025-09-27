package com.example.travelgo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.travelgo.DataBase.AppDatabase
import com.example.travelgo.DataBase.Entidades.Rol
import com.example.travelgo.DataBase.Entidades.Usuario
import com.example.travelgo.Seguridad.HashUtil
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRepetirPassword: EditText
    private lateinit var btnRegistrar: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etRepetirPassword = findViewById(R.id.etRepetirPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        btnRegistrar.setOnClickListener { registrarUsuario() }
    }

    private fun registrarUsuario() {
        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim().lowercase()
        val password = etPassword.text.toString()
        val repetirPassword = etRepetirPassword.text.toString()

        if (nombre.isBlank() || email.isBlank() || password.isBlank() || repetirPassword.isBlank()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != repetirPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)

            val existente = withContext(Dispatchers.IO) {
                db.usuarioDao().buscarPorEmail(email)
            }

            if (existente != null) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Ya existe un usuario con ese email", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val passwordHash = HashUtil.hashPassword(password)

            val nuevoUsuario = Usuario(
                nombre = nombre,
                email = email,
                passwordHash = passwordHash,
                rol = Rol.CLIENTE // 🔹 todos los que se registran son clientes
            )

            withContext(Dispatchers.IO) {
                db.usuarioDao().insertarUsuario(nuevoUsuario)
            }

            runOnUiThread {
                Toast.makeText(this@RegisterActivity, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                // 🔹 Redirigimos al login
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}


