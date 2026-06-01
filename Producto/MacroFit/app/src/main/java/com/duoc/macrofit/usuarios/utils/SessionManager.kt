package com.duoc.macrofit.usuarios.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.duoc.macrofit.usuarios.model.Usuario
import com.google.gson.Gson

object SessionManager {
    private const val PREF_NAME = "MacroFitSession"
    private const val KEY_USER = "usuario_actual"
    private const val KEY_DIARIO = "diario_cache"
    
    private var sharedPreferences: SharedPreferences? = null
    private val gson = Gson()

    var usuarioActual by mutableStateOf<Usuario?>(null)

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val userJson = sharedPreferences?.getString(KEY_USER, null)
        if (userJson != null) {
            usuarioActual = try {
                gson.fromJson(userJson, Usuario::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun guardarSesion(usuario: Usuario) {
        usuarioActual = usuario
        sharedPreferences?.edit()?.putString(KEY_USER, gson.toJson(usuario))?.apply()
    }

    fun limpiarSesion() {
        usuarioActual = null
        sharedPreferences?.edit()?.clear()?.apply()
    }

    fun guardarDiarioCache(jsonDiario: String) {
        sharedPreferences?.edit()?.putString(KEY_DIARIO, jsonDiario)?.apply()
    }

    fun obtenerDiarioCache(): String? {
        return sharedPreferences?.getString(KEY_DIARIO, null)
    }
}
