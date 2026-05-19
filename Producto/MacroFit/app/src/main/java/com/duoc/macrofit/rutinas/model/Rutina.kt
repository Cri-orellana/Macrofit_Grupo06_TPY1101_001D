package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class Rutina(
    @SerializedName("id_rutina")
    val id_rutina: Int,

    @SerializedName("nombre_rutina")
    val nombre_rutina: String,

    val descripcion: String?,

    @SerializedName("activo_catalogo")
    val activo_catalogo: Boolean?
)