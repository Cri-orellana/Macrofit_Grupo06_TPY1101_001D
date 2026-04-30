package com.duoc.macrofit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.api.RetrofitClient
import com.duoc.macrofit.model.NvActividad
import com.duoc.macrofit.model.Objetivo
import com.duoc.macrofit.model.RegistroRequest
import com.duoc.macrofit.utils.SessionManager
import kotlinx.coroutines.launch

class RegistroViewModel : ViewModel() {

    var pasoActual by mutableIntStateOf(1)

    var nombre by mutableStateOf("")
    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var peso by mutableStateOf("")
    var altura by mutableStateOf("")

    var listaObjetivos by mutableStateOf<List<Objetivo>>(emptyList())
    var listaActividades by mutableStateOf<List<NvActividad>>(emptyList())

    var objetivoSeleccionado by mutableStateOf<Objetivo?>(null)
    var actividadSeleccionada by mutableStateOf<NvActividad?>(null)

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var registroExitoso by mutableStateOf(false)

    init {
        cargarCatalogos()
    }

    private fun cargarCatalogos() {
        viewModelScope.launch {
            try {
                val resObjetivos = RetrofitClient.apiService.obtenerObjetivos()
                val resActividades = RetrofitClient.apiService.obtenerActividades()
                if (resObjetivos.isSuccessful) listaObjetivos = resObjetivos.body() ?: emptyList()
                if (resActividades.isSuccessful) listaActividades = resActividades.body() ?: emptyList()
            } catch (e: Exception) {
                errorMessage = "Error conectando con la base de datos."
            }
        }
    }

    fun avanzarPaso() {
        errorMessage = null
        when (pasoActual) {
            1 -> if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) errorMessage = "Completa tus datos" else pasoActual++
            2 -> if (peso.isBlank() || altura.isBlank()) errorMessage = "Ingresa tu peso y altura" else pasoActual++
            3 -> if (objetivoSeleccionado == null) errorMessage = "Selecciona un objetivo" else pasoActual++
            4 -> if (actividadSeleccionada == null) errorMessage = "Selecciona tu nivel de actividad" else registrar()
        }
    }

    fun retrocederPaso() {
        errorMessage = null
        if (pasoActual > 1) pasoActual--
    }

    private fun registrar() {
        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val nuevoRegistro = RegistroRequest(
                    nom_usuario = nombre,
                    correo = correo,
                    contrasena = contrasena,
                    peso = peso.toFloatOrNull() ?: 0f,
                    altura = altura.toFloatOrNull() ?: 0f,
                    id_objetivo = objetivoSeleccionado!!.id_objetivo,
                    id_nv_actividad = actividadSeleccionada!!.id_nv_actividad
                )

                val response = RetrofitClient.apiService.registrarUsuario(nuevoRegistro)
                if (response.isSuccessful && response.body() != null) {
                    registroExitoso = true
                    SessionManager.usuarioActual = response.body()
                } else {
                    errorMessage = "Error al crear cuenta. Quizás el correo ya existe."
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión."
            } finally {
                isLoading = false
            }
        }
    }
}