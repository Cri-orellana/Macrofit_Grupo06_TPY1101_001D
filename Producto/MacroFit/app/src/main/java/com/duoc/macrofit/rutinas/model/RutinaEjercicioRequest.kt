package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class RutinaEjercicioRequest(
    val idRutina: Int,

    val idEjercicio: Int,

    val orden: Int?,
    val series: Int?,
    val repeticiones: Int?,

    val tiempoSeg: Int?,

    val pesoReferencia: Float?
)

