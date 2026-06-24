package com.duoc.macrofit.macros.model

import com.google.gson.annotations.SerializedName

data class ComidaNuevaRequest(
    @SerializedName("code") val code: String?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("calorias") val calorias: Double?,
    @SerializedName("proteinas") val proteinas: Double?,
    @SerializedName("carbohidratos") val carbohidratos: Double?,
    @SerializedName("grasas") val grasas: Double?
)
