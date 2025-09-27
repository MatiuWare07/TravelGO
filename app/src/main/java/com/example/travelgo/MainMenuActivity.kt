package com.example.travelgo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.travelgo.Auth.SessionManager
import com.example.travelgo.DataBase.Entidades.Rol
import com.google.android.material.card.MaterialCardView
import com.google.android.material.button.MaterialButton

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val cardAgregar = findViewById<MaterialCardView>(R.id.cardAgregarLugar)
        val cardVer = findViewById<MaterialCardView>(R.id.cardVerLugares)
        val cardGestionUsuarios = findViewById<MaterialCardView>(R.id.cardGestionUsuarios)
        val btnLogout = findViewById<MaterialButton>(R.id.btnLogout)

        val session = SessionManager(this)
        val usuario = session.obtenerUsuario()

        // 🔹 Validamos sesión
       /** if (usuario == null) {
            startActivity(Intent(this, InicioActivity::class.java))
            finish()
            return
        } **/

        // 🔹 Mostrar opciones según rol
        when (usuario!!.rol) {
            Rol.ADMIN -> {
                cardAgregar.visibility = View.VISIBLE
                cardGestionUsuarios.visibility = View.VISIBLE
                cardVer.visibility = View.VISIBLE
            }
            Rol.CLIENTE -> {
                cardAgregar.visibility = View.GONE
                cardGestionUsuarios.visibility = View.GONE
                cardVer.visibility = View.VISIBLE
            }
        }

        // 🔹 Listeners
        cardAgregar.setOnClickListener {
            if (usuario.rol == Rol.ADMIN) {
                startActivity(Intent(this, AddLugarActivity::class.java))
            } else {
                Toast.makeText(this, "Acceso solo para administradores", Toast.LENGTH_SHORT).show()
            }
        }

        cardVer.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        cardGestionUsuarios.setOnClickListener {
            if (usuario.rol == Rol.ADMIN) {
                startActivity(Intent(this, GestionUsuariosActivity::class.java))
            } else {
                Toast.makeText(this, "Acceso solo para administradores", Toast.LENGTH_SHORT).show()
            }
        }

      btnLogout.setOnClickListener {
            session.cerrarSesion()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
