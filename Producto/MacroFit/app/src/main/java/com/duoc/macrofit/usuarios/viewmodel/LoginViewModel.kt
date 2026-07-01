package com.duoc.macrofit.usuarios.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.usuarios.api.RetrofitClient
import com.duoc.macrofit.usuarios.model.LoginRequest
import com.duoc.macrofit.usuarios.model.LoginResponse
import com.duoc.macrofit.usuarios.utils.SessionManager
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var usuarioLogueado by mutableStateOf<LoginResponse?>(null)

    fun resetState() {
        correo = ""
        contrasena = ""
        usuarioLogueado = null
        errorMessage = null
        isLoading = false
    }

    fun login() {
        if (correo.isBlank() || contrasena.isBlank()) {
            errorMessage = "Por favor, completa todos los campos"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val credenciales = LoginRequest(correo, contrasena)
                val response = RetrofitClient.apiService.login(credenciales)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    SessionManager.guardarSesion(loginResponse)

                    val perfilResponse = RetrofitClient.apiService.obtenerUsuarioPorId(loginResponse.id_usuario)
                    if (perfilResponse.isSuccessful && perfilResponse.body() != null) {
                        SessionManager.guardarSesion(perfilResponse.body()!!)
                        Log.d("MACROFIT_DEBUG", "Perfil completo cargado: ${perfilResponse.body()!!.cal_diaria} kcal")
                    } else {
                        Log.w("MACROFIT_DEBUG", "Login OK pero no se pudo cargar el perfil completo")
                    }

                    usuarioLogueado = loginResponse

                } else {
                    errorMessage = when (response.code()) {
                        401  -> "Correo o contraseña incorrectos."
                        else -> "Error ${response.code()}: No se pudo iniciar sesión."
                    }
                }
            } catch (e: Exception) {
                Log.e("MACROFIT_DEBUG", "ERROR DE RETROFIT EN LOGIN: ", e)
                errorMessage = "No se pudo conectar al servidor: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
