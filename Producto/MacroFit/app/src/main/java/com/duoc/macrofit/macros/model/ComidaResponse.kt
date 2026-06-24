package com.duoc.macrofit.macros.model

import com.google.gson.annotations.SerializedName

data class ComidaResponse (
    val id : Long = 0,
    val userId : String? = null,
    
    @SerializedName(value = "nombre", alternate = ["product_name", "productName"])
    val nombre : String,
    
    @SerializedName(value = "code", alternate = ["barra", "idComida"])
    val code : String,
    
    val porcion : Double = 0.0,
    
    @SerializedName(value = "calorias", alternate = ["energy-kcal_100g", "calories"])
    val calorias : Double,
    
    @SerializedName(value = "proteinas", alternate = ["proteins_100g", "proteins"])
    val proteinas : Double,
    
    @SerializedName(value = "carbohidratos", alternate = ["carbohydrates_100g", "carbohydrates"])
    val carbohidratos : Double,
    
    @SerializedName(value = "grasas", alternate = ["fat_100g", "fat"])
    val grasas : Double,

    val fechareg: String? = null
)
