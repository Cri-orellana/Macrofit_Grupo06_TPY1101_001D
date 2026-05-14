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
}