/**package com.example.travelgo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val btnUsuario = findViewById<Button>(R.id.btnUsuario)
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)

        // Botón Usuario
        btnUsuario.setOnClickListener {
            val intent = Intent(this, MenuAuthActivity::class.java)
            intent.putExtra("ROL", "CLIENTE") // Pasamos el rol
            startActivity(intent)
        }

        // Botón Administrador
        btnAdmin.setOnClickListener {
            val intent = Intent(this, MenuAuthActivity::class.java)
            intent.putExtra("ROL", "ADMIN")
            startActivity(intent)
        }
    }
}

 **/