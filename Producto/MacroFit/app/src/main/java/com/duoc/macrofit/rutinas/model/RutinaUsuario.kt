package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class RutinaUsuario(
    val idRutinaUsuario: Int,

    val idRutina: Int,

    val idUsuario: Int,

    val fechaInicio: String?,

    val fechaFin: String?,

    val activo: Boolean?
)