package com.example.travelgo.Seguridad

import org.mindrot.jbcrypt.BCrypt

object HashUtil {
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun verificarPassword(password: String, hash: String): Boolean {
        return BCrypt.checkpw(password, hash)
    }
}
