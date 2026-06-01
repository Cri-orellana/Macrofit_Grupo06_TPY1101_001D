package com.duoc.macrofit.macros.api

import com.duoc.macrofit.macros.model.ComidaDto
import com.duoc.macrofit.macros.model.ComidaResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiMacros {

    // Al buscar en OpenFoodFacts recibimos el Dto
    @GET("v1/comidasOpen/buscar")
    suspend fun buscarComidaNombre(
        @Query("nombre") nombre: String
    ): Response<List<ComidaDto>>

    @GET("v1/comidasOpen/barra/{code}")
    suspend fun buscarComidaBarra(
        @Path("code") code: String
    ): Response<ComidaDto>

    @POST("v1/diario/agregar")
    suspend fun agregarAlDiario(
        @Body comidaDto: ComidaDto,
        @Query("porcion") porcion: Double,
        @Query("userId") usuarioId: String
    ): Response<ComidaResponse>

    @GET("v1/diario/usuario/{userId}")
    suspend fun obtenerDiario(
        @Path("userId") userId: String
    ): Response<List<ComidaResponse>>

    @DELETE("v1/diario/eliminar/{id}")
    suspend fun eliminarComida(
        @Path("id") id: Long
    ): Response<Void>

    @POST("v1/diario/actualizar/{id}")
    suspend fun actualizarComida(
        @Path("id") id: Long,
        @Query("porcion") porcion: Double
    ): Response<ComidaResponse>

    // Historial y Cierre
    @POST("v1/historialComidas/cerrar-dia")
    suspend fun cerrarDiaManual(): Response<Void>

    @GET("v1/historialComidas/Usuario/{userId}")
    suspend fun obtenerHistorial(
        @Path("userId") userId: String
    ): Response<List<ComidaResponse>>
}