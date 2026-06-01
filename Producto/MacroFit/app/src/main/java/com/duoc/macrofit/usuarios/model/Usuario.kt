package com.duoc.macrofit.usuarios.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id_usuario") val id: Int,
    val nom_usuario: String,
    val correo: String,
    val peso: Float,
    val altura: Int?,
    val cal_diaria: Float?,
    val rol: String,
    val edad: Int? = null,
    val id_objetivo: Int? = null,
    val id_nv_act: Int? = null,
    val tmb_objetivo: Float? = null
)