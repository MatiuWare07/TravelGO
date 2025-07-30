package com.example.travelgo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LugarDetalleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lugar_detalle)

        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val categoria = intent.getStringExtra("categoria")
        val latitud = intent.getDoubleExtra("latitud", 0.0)
        val longitud = intent.getDoubleExtra("longitud", 0.0)
        val imagenResId = intent.getIntExtra("imagenResId", R.drawable.ic_launcher_foreground)

        findViewById<TextView>(R.id.txtNombreDetalle).text = nombre
        findViewById<TextView>(R.id.txtCategoriaDetalle).text = categoria
        findViewById<TextView>(R.id.txtDescripcionDetalle).text = descripcion
        findViewById<ImageView>(R.id.imgDetalle).setImageResource(imagenResId)

        findViewById<Button>(R.id.btnVerEnMapa).setOnClickListener {
            val uri = Uri.parse("geo:$latitud,$longitud?q=$latitud,$longitud($nombre)")
            val intentMapa = Intent(Intent.ACTION_VIEW, uri)

            // Si querés que solo se abra con Google Maps (si está instalado):
            // intentMapa.setPackage("com.google.android.apps.maps")

            // Verificamos si hay alguna app capaz de manejar el intent
            if (intentMapa.resolveActivity(packageManager) != null) {
                startActivity(intentMapa)
            } else {
                Toast.makeText(this, "No hay app de mapas instalada", Toast.LENGTH_LONG).show()
            }
        }

    }
}
