package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class Ejercicio(
    val idEjercicio: Int,

    val nombreEjercicio: String,

    val descripcion: String?,

    val imagenEjercicio: String?,

    val videoEjercicio: String?,

    val zonaMuscular: String?,

    val implemento: String?,

    val nivelDificultad: String?,

    val musculoObjetivo: String?,

    val activoCatalogo: Boolean?
)