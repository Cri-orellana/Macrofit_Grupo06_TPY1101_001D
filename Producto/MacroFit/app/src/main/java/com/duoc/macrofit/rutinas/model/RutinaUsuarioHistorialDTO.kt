package com.duoc.macrofit.rutinas.model

data class RutinaUsuarioHistorialDTO(
    val idRutinaUsuario: Int,
    val idRutina: Int,
    val nombreRutina: String?,
    val fechaInicio: String,
    val fechaFin: String?,
    val activo: Boolean
)