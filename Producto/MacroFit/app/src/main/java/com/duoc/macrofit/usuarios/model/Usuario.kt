package com.duoc.macrofit.usuarios.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id_usuario")
    val id: Int = 0,
    val nom_usuario: String = "",
    val correo: String = "",
    val rol: String = "",
    val edad: Int? = null,
    val peso: Float = 0f,
    val altura: Int? = null,
    val sexo: String? = null,
    val id_objetivo: Int? = null,
    val id_nv_act: Int? = null,
    val tmb_objetivo: Float = 0f,
    val cal_diaria: Float = 0f
)