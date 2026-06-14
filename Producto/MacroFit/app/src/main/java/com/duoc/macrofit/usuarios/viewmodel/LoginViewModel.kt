package com.duoc.macrofit.usuarios.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.usuarios.api.RetrofitClient
import com.duoc.macrofit.usuarios.model.LoginRequest
import com.duoc.macrofit.usuarios.model.Usuario
import com.duoc.macrofit.usuarios.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Variables de estado que la pantalla (UI) estará observando
    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var usuarioLogueado by mutableStateOf<Usuario?>(null)

    fun login() {
        if (correo.isBlank() || contrasena.isBlank()) {
            errorMessage = "Por favor, completa todos los campos"
            return
        }

        isLoading = true
        errorMessage = null

        Log.d(
            "MACROFIT_DEBUG",
            "Intentando conectar a: " + com.duoc.macrofit.usuarios.utils.Constants.BASE_URL
        )

        viewModelScope.launch {
            try {
                val credenciales = LoginRequest(correo, contrasena)
                val response = RetrofitClient.apiService.login(credenciales)

                if (response.isSuccessful && response.body() != null) {
                    usuarioLogueado = response.body()
                    SessionManager.guardarSesion(response.body()!!)
                } else {
                    val codigoError = response.code()
                    val cuerpoError = response.errorBody()?.string()

                    Log.e("MACROFIT_DEBUG", "El servidor respondió pero rechazó el login. Código: $codigoError, Detalle: $cuerpoError")

                    errorMessage = "Error $codigoError: Credenciales incorrectas o servidor rechazó la petición."
                }
            } catch (e: Exception) {
                Log.e("MACROFIT_DEBUG", "ERROR REAL DE RETROFIT: ", e)
                errorMessage = "No se pudo conectar al servidor: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}