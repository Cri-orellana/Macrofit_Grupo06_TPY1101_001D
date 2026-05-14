package com.duoc.macrofit.nutricion.api

import com.duoc.macrofit.nutricion.model.ComidaRecomendada
import com.duoc.macrofit.nutricion.model.TipoAlimentacion
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiNutricion {

    @GET("nutricion/tipos-dieta")
    suspend fun obtenerTiposDieta(): List<TipoAlimentacion>

    @GET("nutricion/comidas")
    suspend fun obtenerComidasPorTipo(
        @Query("tipoId") id_tipo_alimentacion: Int
    ): List<ComidaRecomendada>

    @GET("nutricion/recomendaciones")
    suspend fun obtenerRecomendaciones(
        @Query("tipoDieta") tipoDieta: String? = null,
        @Query("ingredientes") ingredientes: String? = null,
        @Query("maxCarbohidratos") maxCarbohidratos: Float? = null,
        @Query("minProteina") minProteina: Float? = null,
        @Query("maxGrasa") maxGrasa: Float? = null
    ): List<ComidaRecomendada>
}