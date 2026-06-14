package com.duoc.macrofit.usuarios.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.usuarios.api.RetrofitClient
import com.duoc.macrofit.usuarios.model.NvActividad
import com.duoc.macrofit.usuarios.model.Objetivo
import com.duoc.macrofit.usuarios.model.RegistroRequest
import com.duoc.macrofit.usuarios.utils.SessionManager
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class RegistroViewModel : ViewModel() {

    var pasoActual by mutableIntStateOf(1)

    var nombre by mutableStateOf("")
    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var edad by mutableStateOf("")
    var peso by mutableStateOf("")
    var altura by mutableStateOf("")
    var sexo by mutableStateOf("")

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
                Log.e("MACROFIT_DEBUG", "EXPLOTÓ CARGAR CATALOGOS: ${e.message}", e)
                errorMessage = "Error conectando con la base de datos."
            }
        }
    }

    fun avanzarPaso() {
        errorMessage = null
        when (pasoActual) {
            1 -> {
                // Al menos 1 letra, al menos 1 número, mínimo 8 caracteres
                val passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$".toRegex()

                if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                    errorMessage = "Completa tus datos"
                } else if (!contrasena.matches(passwordRegex)) {
                    errorMessage = "La contraseña debe tener mínimo 8 caracteres (solo letras y números)"
                } else {
                    pasoActual++
                }
            }
            2 -> if (edad.isBlank() || peso.isBlank() || altura.isBlank() || sexo.isBlank())
                errorMessage = "Completa todos tus datos físicos, incluyendo el sexo"
            else pasoActual++
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
                    edad = edad.toIntOrNull() ?: 0,
                    peso = peso.toFloatOrNull() ?: 0f,
                    altura = altura.toIntOrNull() ?: 0,
                    sexo = if (sexo == "M") "Masculino" else "Femenino",
                    id_objetivo = objetivoSeleccionado!!.id_objetivo,
                    id_nv_actividad = actividadSeleccionada!!.id_nv_actividad
                )

                val response = RetrofitClient.apiService.registrarUsuario(nuevoRegistro)
                if (response.isSuccessful && response.body() != null) {
                    registroExitoso = true
                    SessionManager.guardarSesion(response.body()!!)
                } else {
                    val mensajeErrorServidor = response.errorBody()?.string()
                    errorMessage = if (!mensajeErrorServidor.isNullOrEmpty()) {
                        mensajeErrorServidor
                    } else {
                        "Error desconocido al crear la cuenta."
                    }
                }
            } catch (e: Exception) {
                Log.e("MACROFIT_DEBUG", "EXPLOTÓ REGISTRAR: ${e.message}", e)
                errorMessage = "Error de conexión."
            } finally {
                isLoading = false
            }
        }
    }

    fun limpiarFormulario() {
        pasoActual = 1
        nombre = ""
        correo = ""
        contrasena = ""
        edad = ""
        peso = ""
        altura = ""
        objetivoSeleccionado = null
        actividadSeleccionada = null
        registroExitoso = false
        errorMessage = null
    }
}