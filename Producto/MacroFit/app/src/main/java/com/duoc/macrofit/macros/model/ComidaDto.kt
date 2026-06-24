package com.duoc.macrofit.macros.model
import com.google.gson.annotations.SerializedName

data class ComidaDto (
    @SerializedName(value = "code", alternate = ["barra", "idComida"]) 
    val barra: String?,
    
    @SerializedName(value = "product_name", alternate = ["nombre", "productName"]) 
    val nombre: String?,
    
    @SerializedName("nutriments") 
    val nutriments: Nutriments?
)

data class Nutriments(
    @SerializedName(value = "energy-kcal_100g", alternate = ["calorias", "energy", "calories"]) 
    val calorias: Double?,
    
    @SerializedName(value = "carbohydrates_100g", alternate = ["carbohidratos", "carbohydrates"]) 
    val carbohidratos: Double?,
    
    @SerializedName(value = "proteins_100g", alternate = ["proteinas", "proteins"]) 
    val proteinas: Double?,
    
    @SerializedName(value = "fat_100g", alternate = ["grasas", "fat"]) 
    val grasas: Double?
)
