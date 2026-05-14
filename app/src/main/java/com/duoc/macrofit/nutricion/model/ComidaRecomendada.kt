package com.duoc.macrofit.nutricion.model

data class ComidaRecomendada(
    val id_comida: Int,
    val nombre_comida: String,
    val descripcion_comida: String?,
    val calorias_porcion: Float,
    val proteina_porcion: Float,
    val carbohidratos_porcion: Float,
    val grasa_porcion: Float,
    val tipo_alimentacion: TipoAlimentacion? = null
)