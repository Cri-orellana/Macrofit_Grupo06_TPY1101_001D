package com.duoc.macrofit.macros.api

import com.duoc.macrofit.macros.model.ComidaDto
import com.duoc.macrofit.macros.model.ComidaNuevaRequest
import com.duoc.macrofit.macros.model.ComidaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiMacros {

    // Búsqueda en base de datos propia
    @GET("api/v1/comidas/buscar")
    suspend fun buscarComidaPropia(@Query("nombre") nombre: String): Response<List<ComidaResponse>>

    // Búsqueda en OpenFoodFacts (vía backend)
    @GET("api/v1/comidasOpen/buscar")
    suspend fun buscarComidaOpen(@Query("nombre") nombre: String): Response<List<ComidaDto>>

    @GET("api/v1/comidasOpen/barra/{code}")
    suspend fun buscarComidaBarraOpen(@Path("code") code: String): Response<ComidaDto>

    @GET("api/v1/comidas/{code}")
    suspend fun buscarComidaBarraPropia(@Path("code") code: String): Response<ComidaResponse>

    @POST("api/v1/diario/agregar")
    suspend fun agregarAlDiario(
        @Body comidaDto: ComidaDto,
        @Query("porcion") porcion: Double,
        @Query("userId") usuarioId: String
    ): Response<ComidaResponse>

    @GET("api/v1/diario/usuario/{userId}")
    suspend fun obtenerDiario(
        @Path("userId") userId: String
    ): Response<List<ComidaResponse>>

    // Endpoints para guardar alimentos nuevos (OCR) en nuestra BD
    @POST("api/v1/comidas")
    suspend fun guardarComidaNueva(
        @Body comida: ComidaNuevaRequest
    ): Response<ComidaNuevaRequest>

    @DELETE("api/v1/diario/eliminar/{id}")
    suspend fun eliminarComida(
        @Path("id") id: Long
    ): Response<Void>

    @POST("api/v1/diario/actualizar/{id}")
    suspend fun actualizarComida(
        @Path("id") id: Long,
        @Query("porcion") porcion: Double
    ): Response<ComidaResponse>

    // Historial y Cierre
    @POST("api/v1/historialComidas/cerrar-dia")
    suspend fun cerrarDiaManual(@Query("userId") userId: String): Response<Void>

    @GET("api/v1/historialComidas/Usuario/{userId}")
    suspend fun obtenerHistorial(
        @Path("userId") userId: String
    ): Response<List<ComidaResponse>>
}
