package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class CrearRutinaRequest(
    val nombreRutina: String,

    val descripcion: String?
)
