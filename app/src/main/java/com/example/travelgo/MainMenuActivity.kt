package com.example.travelgo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val btnAgregar = findViewById<Button>(R.id.btnAgregarLugar)
        val btnVer = findViewById<Button>(R.id.btnVerLugares)

        btnAgregar.setOnClickListener {
            val intent = Intent(this, AddLugarActivity::class.java)
            startActivity(intent)
        }

        btnVer.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
