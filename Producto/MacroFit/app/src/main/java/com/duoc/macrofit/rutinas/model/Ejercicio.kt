package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class Ejercicio(
    @SerializedName("id_ejercicio")
    val id_ejercicio: Int,

    @SerializedName("id_zona")
    val id_zona: Int?,

    @SerializedName("id_implemento")
    val id_implemento: Int?,

    @SerializedName("nombre_ejercicio")
    val nombre_ejercicio: String,

    val descripcion: String?,

    @SerializedName("imagen_ejercicio")
    val imagen_ejercicio: String?,

    @SerializedName("video_ejercicio")
    val video_ejercicio: String?,

    @SerializedName("activo_catalogo")
    val activo_catalogo: Boolean?
)