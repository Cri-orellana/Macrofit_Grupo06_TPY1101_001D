package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class RutinaEjercicio(
    val idRutinaEjercicio: Int,

    val idEjercicio: Int,

    val idRutina: Int,

    val dia: Int? = 1,
    val orden: Int? = 1,

    val series: Int?,
    val repeticiones: Int?,
    val tiempoSeg: Int?,
    val pesoReferencia: Float?
)