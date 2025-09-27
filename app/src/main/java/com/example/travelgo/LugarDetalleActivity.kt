package com.example.travelgo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import java.io.File

class LugarDetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lugar_detalle)

        // 🔹 Configuración del toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarLugarDetalle)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // 🔹 Obtener datos del intent
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val categoria = intent.getStringExtra("categoria")
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)
        val imagenUri = intent.getStringExtra("imagenUri")
        val imagenResId = intent.getIntExtra("imagenResId", -1)

        // 🔹 Referencias de UI
        findViewById<TextView>(R.id.txtNombreDetalle).text = nombre
        findViewById<TextView>(R.id.txtCategoriaDetalle).text = categoria
        findViewById<TextView>(R.id.txtDescripcionDetalle).text = descripcion
        val imgLugar = findViewById<ImageView>(R.id.imgDetalle)

        // 🔹 Cargar imagen con Glide
        when {
            !imagenUri.isNullOrEmpty() -> {
                Glide.with(this)
                    .load(File(imagenUri))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imgLugar)
            }
            imagenResId != -1 -> {
                Glide.with(this)
                    .load(imagenResId)
                    .placeholder(R.drawable.placeholder)
                    .into(imgLugar)
            }
            else -> imgLugar.setImageResource(R.drawable.placeholder)
        }

        // 🔹 Botón abrir en mapa
        findViewById<MaterialButton>(R.id.btnVerEnMapa).setOnClickListener {
            val uri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud($nombre)")
            val intentMapa = Intent(Intent.ACTION_VIEW, uri)

            if (intentMapa.resolveActivity(packageManager) != null) {
                startActivity(intentMapa)
            } else {
                Toast.makeText(this, "No hay app de mapas instalada", Toast.LENGTH_LONG).show()
            }
        }
    }
}
