package com.duoc.macrofit.macros.api

import com.duoc.macrofit.macros.model.ComidaDto
import com.duoc.macrofit.macros.model.ComidaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
interface ApiMacros {
    // Cambiamos "api/v1/..." por "v1/..."
    @GET("v1/comidasOpen/buscar")
    suspend fun buscarComidaNombre(
        @Query("nombre") nombre: String
    ): Response<List<ComidaResponse>>

    @GET("v1/comidasOpen/barra/{code}")
    suspend fun buscarComidaBarra(
        @Path("code") code: String
    ): Response<ComidaDto>

    @POST("v1/diario/agregar")
    suspend fun agregarAlDiario(
        @Body comidaDto: ComidaDto,
        @Query("gramos") porcion: Double,
        @Query("userId") usuarioId: String
    ): Response<ComidaResponse>

    @GET("v1/diario/usuario/{userId}")
    suspend fun obtenerDiario(
        @Path("userId") userId: String
    ): Response<List<ComidaResponse>>
}