package com.duoc.macrofit.usuarios.model

import com.google.gson.annotations.SerializedName

data class RegistroRequest(
    val nom_usuario: String,
    val correo: String,
    val contrasena: String,
    val edad: Int,
    val peso: Float,
    val altura: Int,
    val sexo: String,
    val id_objetivo: Int,
    @SerializedName("id_nv_act")
    val id_nv_actividad: Int
)