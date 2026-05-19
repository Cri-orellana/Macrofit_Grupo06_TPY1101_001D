package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class RutinaEjercicio(
    @SerializedName("id_rutina_ejercicio")
    val id_rutina_ejercicio: Int,

    @SerializedName("id_ejercicio")
    val id_ejercicio: Int,

    @SerializedName("id_rutina")
    val id_rutina: Int,

    val orden: Int?,
    val series: Int?,
    val repeticiones: Int?,

    @SerializedName("tiempo_seg")
    val tiempo_seg: Int?,

    @SerializedName("peso_referencia")
    val peso_referencia: Float?
)