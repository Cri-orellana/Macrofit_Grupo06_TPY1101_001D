package com.duoc.macrofit.model

data class RegistroRequest(
    val nom_usuario: String,
    val correo: String,
    val contrasena: String,
    val peso: Float,
    val altura: Float,
    val id_objetivo: Int,
    val id_nv_actividad: Int
)