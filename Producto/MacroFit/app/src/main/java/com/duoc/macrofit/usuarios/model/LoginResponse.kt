package com.duoc.macrofit.usuarios.model


data class LoginResponse(
    val token: String,
    val id_usuario: Int,
    val nom_usuario: String,
    val correo: String,
    val rol: String
)
