package com.duoc.macrofit.usuarios.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.usuarios.api.RetrofitClient
import com.duoc.macrofit.usuarios.model.Usuario
import com.duoc.macrofit.usuarios.utils.SessionManager
import kotlinx.coroutines.launch

class PerfilViewModel : ViewModel() {

    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    // Función que llama a Retrofit
    fun actualizarPerfilServidor(
        nombre: String,
        correo: String,
        edad: Int,
        peso: Float,
        altura: Int,
        idNivelActividad: Int,
        idObjetivo: Int,
        alTerminar: (Usuario) -> Unit
    ) {
        val usuarioActual = SessionManager.usuarioActual ?: return

        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                val datosNuevos = usuarioActual.copy(
                    nom_usuario = nombre,
                    correo = correo,
                    edad = edad,
                    peso = peso,
                    altura = altura,
                    id_nv_act = idNivelActividad,
                    id_objetivo = idObjetivo
                )

                val respuesta = RetrofitClient.apiService.actualizarPerfilUsuario(usuarioActual.id, datosNuevos)

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val usuarioActualizado = respuesta.body()!!
                    SessionManager.usuarioActual = usuarioActualizado
                    alTerminar(usuarioActualizado)
                } else {
                    mensajeError = "Error al guardar los cambios en el servidor."
                }
            } catch (e: Exception) {
                mensajeError = "Error de red: Verifica tu conexión a internet."
            } finally {
                cargando = false
            }
        }
    }
}