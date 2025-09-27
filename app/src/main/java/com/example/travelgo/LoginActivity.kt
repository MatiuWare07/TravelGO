package com.example.travelgo

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.travelgo.Auth.SessionManager
import com.example.travelgo.DataBase.AppDatabase
import com.example.travelgo.DataBase.Entidades.Rol
import com.example.travelgo.Seguridad.HashUtil
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvRegistrarse: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegistrarse = findViewById(R.id.tvRegistrarse)

        // 🔹 Botón Login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim().lowercase()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                validarLogin(email, password)
            }
        }

        // 🔹 Link a registro (solo crea CLIENTES)
        tvRegistrarse.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validarLogin(email: String, password: String) {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            val usuario = withContext(Dispatchers.IO) {
                db.usuarioDao().buscarPorEmail(email)
            }

            if (usuario == null) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val valido = HashUtil.verificarPassword(password, usuario.passwordHash)

            if (valido) {
                // Guardamos sesión
                SessionManager(this@LoginActivity).guardarUsuario(usuario)

                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login correcto", Toast.LENGTH_SHORT).show()

                    when (usuario.rol) {
                        Rol.ADMIN -> startActivity(Intent(this@LoginActivity, MainMenuActivity::class.java))
                        Rol.CLIENTE -> startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                    finish()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

