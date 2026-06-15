package com.duoc.macrofit.nutricion.model

data class ComidaRecomendada(
    val id: Int? = null,
    val id_comida: Int? = null,
    val nombre_comida: String,
    val descripcion_comida: String? = null,
    val calorias_porcion: Float,
    val proteina_porcion: Float,
    val carbohidratos_porcion: Float,
    val grasa_porcion: Float,
    val tipo_alimentacion: TipoAlimentacion? = null,
    val foto_comida: String? = null,
    val ingredientes_lista: List<String>? = emptyList(),
    val preparacion_lista: List<String>? = emptyList(),
    val cantidad_porcion: String? = null,
    val ingredientes_json: String? = null,
    val preparacion_json: String? = null,
    val cacheKey: String? = null,
)