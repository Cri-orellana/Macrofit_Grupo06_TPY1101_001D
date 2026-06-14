package com.duoc.macrofit.usuarios.utils

import android.content.Context
import android.content.SharedPreferences
import com.duoc.macrofit.usuarios.model.Usuario
import com.google.gson.Gson

object SessionManager {
    private const val PREF_NAME = "MacrofitSession"
    private const val KEY_USUARIO = "usuario_actual"
    private const val KEY_DIARIO_CACHE = "diario_cache"

    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    var usuarioActual: Usuario? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val usuarioJson = prefs?.getString(KEY_USUARIO, null)
        if (usuarioJson != null) {
            usuarioActual = gson.fromJson(usuarioJson, Usuario::class.java)
        }
    }

    fun guardarSesion(usuario: Usuario) {
        usuarioActual = usuario
        prefs?.edit()?.putString(KEY_USUARIO, gson.toJson(usuario))?.apply()
    }

    fun cerrarSesion() {
        usuarioActual = null
        prefs?.edit()?.remove(KEY_USUARIO)?.apply()
    }

    // ↓ Agregar estos dos métodos
    fun guardarDiarioCache(cache: String) {
        prefs?.edit()?.putString(KEY_DIARIO_CACHE, cache)?.apply()
    }

    fun obtenerDiarioCache(): String {
        return prefs?.getString(KEY_DIARIO_CACHE, "") ?: ""
    }


}