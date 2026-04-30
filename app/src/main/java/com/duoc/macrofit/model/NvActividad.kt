package com.duoc.macrofit.model

import com.google.gson.annotations.SerializedName

data class NvActividad(
    @SerializedName("id_nv_act")
    val id_nv_actividad: Int,

    @SerializedName("desc_nv_act")
    val nombre_actividad: String,

    val multiplicador: Float
)