package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class RutinaUsuarioHistorialDTO(
    val idRutinaUsuario: Int,
    val idRutina: Int,
    @SerializedName("nombre_rutina")
    val nombreRutina: String?,
    val fechaInicio: String,
    val fechaFin: String?,
    val activo: Boolean
)