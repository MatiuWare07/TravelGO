package com.example.travelgo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.travelgo.DataBase.AppDatabase
import com.example.travelgo.DataBase.Entidades.LugarTuristico
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class AddLugarActivity : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etCategoria: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var tvCoordenadas: TextView
    private lateinit var btnSeleccionarUbicacion: Button
    private lateinit var btnGuardarLugar: Button

    private var imagenUri: String? = null
    private var latitud: Double? = null
    private var longitud: Double? = null

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val REQUEST_LOCATION_PICK = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lugar)

        etNombre = findViewById(R.id.etNombre)
        etCategoria = findViewById(R.id.etCategoria)
        etDescripcion = findViewById(R.id.etDescripcion)
        imgPreview = findViewById(R.id.imgPreview)
        btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen)
        tvCoordenadas = findViewById(R.id.tvCoordenadas)
        btnSeleccionarUbicacion = findViewById(R.id.btnSeleccionarUbicacion)
        btnGuardarLugar = findViewById(R.id.btnGuardarLugar)

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        btnSeleccionarUbicacion.setOnClickListener {
            val intent = Intent(this, SelectLocationActivity::class.java)
            startActivityForResult(intent, REQUEST_LOCATION_PICK)
        }

        btnGuardarLugar.setOnClickListener { guardarLugar() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val uri = data?.data ?: return

                    // Guardamos copia en almacenamiento interno
                    val file = File(filesDir, "img_${System.currentTimeMillis()}.jpg")
                    contentResolver.openInputStream(uri)?.use { input ->
                        FileOutputStream(file).use { output ->
                            input.copyTo(output)
                        }
                    }

                    // Guardar la ruta absoluta (sin file://)
                    imagenUri = file.absolutePath

                    // Mostrar preview con Glide
                    Glide.with(this)
                        .load(file) // 👈 Cargar directamente el File
                        .placeholder(R.drawable.placeholder)
                        .into(imgPreview)
                }

                REQUEST_LOCATION_PICK -> {
                    latitud = data?.getDoubleExtra("latitud", 0.0)
                    longitud = data?.getDoubleExtra("longitud", 0.0)
                    tvCoordenadas.text = "Coordenadas: $latitud, $longitud"
                }
            }
        }
    }

    private fun guardarLugar() {
        val nombre = etNombre.text.toString().trim()
        val categoria = etCategoria.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()

        if (nombre.isBlank() || latitud == null || longitud == null) {
            Toast.makeText(
                this,
                "Completa todos los campos y selecciona ubicación",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val nuevoLugar = LugarTuristico(
            nombre = nombre,
            categoria = categoria,
            descripcion = descripcion,
            latitud = latitud!!,
            longitud = longitud!!,
            imagenUri = imagenUri
        )

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            db.lugarTuristicoDao().insertarLugar(nuevoLugar)

            runOnUiThread {
                Toast.makeText(this@AddLugarActivity, "Lugar guardado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}













