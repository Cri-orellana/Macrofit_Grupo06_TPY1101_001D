package com.duoc.macrofit.usuarios.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.duoc.macrofit.usuarios.model.Usuario

object SessionManager {
    var usuarioActual by mutableStateOf<Usuario?>(null)

    fun limpiarSesion() {
        usuarioActual = null
    }
}