package com.duoc.macrofit.model

data class Usuario(
    val id: Int,
    val nom_usuario: String,
    val correo: String,
    val peso: Float,
    val altura: Float?,
    val cal_diaria: Float?,
    val rol: String
)