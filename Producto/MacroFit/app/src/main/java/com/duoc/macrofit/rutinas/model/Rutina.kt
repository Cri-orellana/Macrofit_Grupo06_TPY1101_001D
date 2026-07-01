package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class Rutina(
    val idRutina: Int,

    val idUsuarioCreador: Int?,

    val nombreRutina: String,

    val descripcion: String?,

    val cantidadDias: Int? = 1,

    val esBase: Boolean?,

    val activoCatalogo: Boolean?
)