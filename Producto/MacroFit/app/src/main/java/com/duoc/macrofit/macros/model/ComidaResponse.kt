package com.duoc.macrofit.macros.model

data class ComidaResponse (
    val id : Long,
    val userId : String,
    val nombre : String,
    val code : String,
    val porcion : Double,
    val calorias : Double,
    val proteinas : Double,
    val carbohidratos : Double,
    val grasas : Double
)
