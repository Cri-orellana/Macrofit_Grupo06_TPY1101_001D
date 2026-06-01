package com.duoc.macrofit.macros.model
import com.google.gson.annotations.SerializedName

data class ComidaDto (
    @SerializedName("code") val barra: String?,
    @SerializedName("product_name") val nombre: String?,
    @SerializedName("nutriments") val nutriments: Nutriments?
)

data class Nutriments(
    @SerializedName("energy-kcal_100g") val calorias: Double?,
    @SerializedName("carbohydrates_100g") val carbohidratos: Double?,
    @SerializedName("proteins_100g") val proteinas: Double?,
    @SerializedName("fat_100g") val grasas: Double?
)