package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class AsignarRutinaRequest(
    val idRutina: Int,

    val idUsuario: Int,

    val fechaInicio: String
)