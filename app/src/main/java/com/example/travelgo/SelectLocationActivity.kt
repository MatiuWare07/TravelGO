package com.example.travelgo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay

class SelectLocationActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var selectedPoint: GeoPoint? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración osmdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        setContentView(R.layout.activity_select_location)

        // Configurar mapa
        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val mapController: IMapController = mapView.controller
        mapController.setZoom(12.0)
        mapController.setCenter(GeoPoint(40.4168, -3.7038)) // Madrid como inicio

        // Overlay para detectar taps/long presses
        val eventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                // Ignoramos single tap
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    selectedPoint = p

                    // Eliminar marcador anterior
                    marker?.let { mapView.overlays.remove(it) }

                    // Crear nuevo marcador
                    marker = Marker(mapView).apply {
                        position = p
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Ubicación seleccionada"
                    }

                    mapView.overlays.add(marker)
                    mapView.invalidate()
                }
                return true
            }
        }
        mapView.overlays.add(MapEventsOverlay(eventsReceiver))

        // Botón confirmar
        val btnConfirmar = findViewById<FloatingActionButton>(R.id.btnConfirmarUbicacion)
        btnConfirmar.setOnClickListener {
            if (selectedPoint != null) {
                val resultIntent = Intent().apply {
                    putExtra("latitud", selectedPoint!!.latitude)
                    putExtra("longitud", selectedPoint!!.longitude)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Mantené presionado en el mapa para seleccionar una ubicación",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

