package com.duoc.macrofit.usuarios.model

import com.google.gson.annotations.SerializedName

data class Objetivo(
    @SerializedName("id_objetivo")
    val id_objetivo: Int,

    @SerializedName("descrip_obj")
    val nom_objetivo: String,

    @SerializedName("ajuste_calorico")
    val calorias: Float
)