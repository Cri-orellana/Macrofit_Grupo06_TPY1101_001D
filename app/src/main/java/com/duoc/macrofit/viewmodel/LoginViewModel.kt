package com.duoc.macrofit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.api.RetrofitClient
import com.duoc.macrofit.model.LoginRequest
import com.duoc.macrofit.model.Usuario
import com.duoc.macrofit.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Variables de estado que la pantalla (UI) estará observando
    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var usuarioLogueado by mutableStateOf<Usuario?>(null)

    // Función que se ejecuta al presionar el botón "Ingresar"
    fun login() {
        // Validación básica antes de ir a internet
        if (correo.isBlank() || contrasena.isBlank()) {
            errorMessage = "Por favor, completa todos los campos"
            return
        }

        isLoading = true
        errorMessage = null

        // viewModelScope.launch crea la "corrutina" para no congelar la app
        viewModelScope.launch {
            try {
                val credenciales = LoginRequest(correo, contrasena)
                val response = RetrofitClient.apiService.login(credenciales)

                if (response.isSuccessful && response.body() != null) {
                    // HTTP 200: Login exitoso
                    usuarioLogueado = response.body()
                    SessionManager.usuarioActual = response.body()
                } else {
                    // HTTP 401 u otro error de credenciales
                    errorMessage = "Credenciales incorrectas. Inténtalo de nuevo."
                }
            } catch (e: Exception) {
                // Error de red (el backend está apagado, no hay internet, etc.)
                errorMessage = "Error de conexión: Verifica que el servidor esté encendido."
            } finally {
                // Pase lo que pase, apagamos el circulito de carga
                isLoading = false
            }
        }
    }
}