package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class Rutina(
    val idRutina: Int,

    val nombreRutina: String,

    val descripcion: String?,

    val activoCatalogo: Boolean?
)