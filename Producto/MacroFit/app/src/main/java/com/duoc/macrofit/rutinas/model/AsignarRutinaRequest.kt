package com.duoc.macrofit.rutinas.model

import com.google.gson.annotations.SerializedName

data class AsignarRutinaRequest(
    @SerializedName("id_rutina")
    val id_rutina: Int,

    @SerializedName("id_usuario")
    val id_usuario: Int,

    @SerializedName("fecha_inicio")
    val fecha_inicio: String
)