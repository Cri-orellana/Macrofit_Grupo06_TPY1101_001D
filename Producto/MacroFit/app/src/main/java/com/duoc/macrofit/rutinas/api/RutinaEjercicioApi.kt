package com.duoc.macrofit.rutinas.api

import com.duoc.macrofit.rutinas.model.RutinaEjercicioRequest
import com.duoc.macrofit.rutinas.model.CrearRutinaRequest
import com.duoc.macrofit.rutinas.model.AsignarRutinaRequest
import com.duoc.macrofit.rutinas.model.Ejercicio
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.rutinas.model.RutinaEjercicio
import com.duoc.macrofit.rutinas.model.RutinaUsuario
import retrofit2.http.PUT
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RutinaEjercicioApi {


    // ---- RUTINAS ----
    @GET("rutinas/activas")
    suspend fun obtenerRutinasActivas(): Response<List<Rutina>>

    @GET("rutinas/usuario/{idUsuario}")
    suspend fun obtenerRutinasPorUsuario(@Path("idUsuario") idUsuario: Int
    ): Response<List<Rutina>>

    @GET("rutinas/{id}")
    suspend fun obtenerRutinaPorId(@Path("id") id: Int): Response<Rutina>

    // ---- EJERCICIOS ----
    @GET("ejercicios/{id}")
    suspend fun obtenerEjercicioPorId(@Path("id") id: Int): Response<Ejercicio>

    // ---- RUTINA-EJERCICIO ----
    @GET("rutina-ejercicio/rutina/{idRutina}")
    suspend fun obtenerEjerciciosDeRutina(
        @Path("idRutina") idRutina: Int
    ): Response<List<RutinaEjercicio>>

    // ---- RUTINA-USUARIO ----
    @GET("rutina-usuario/usuario/{idUsuario}/activa")
    suspend fun obtenerRutinaActiva(
        @Path("idUsuario") idUsuario: Int
    ): Response<RutinaUsuario>

    @POST("rutina-usuario")
    suspend fun asignarRutina(
        @Body request: AsignarRutinaRequest
    ): Response<RutinaUsuario>

    @PATCH("rutina-usuario/{id}/desactivar")
    suspend fun desactivarAsignacion(
        @Path("id") idAsignacion: Int
    ): Response<RutinaUsuario>

    // ---- FILTROS DE EJERCICIOS ----
    @GET("ejercicios/filtrar")
    suspend fun filtrarEjercicios(
        @Query("zonaMuscular") zonaMuscular: String? = null,
        @Query("implemento") implemento: String? = null,
        @Query("nivelDificultad") nivelDificultad: String? = null,
        @Query("musculoObjetivo") musculoObjetivo: String? = null
    ): Response<List<Ejercicio>>

    // ---- AGREGAR EJERCICIO A RUTINA ----
    @POST("rutina-ejercicio")
    suspend fun agregarEjercicioARutina(
        @Body rutinaEjercicio: RutinaEjercicio
    ): Response<RutinaEjercicio>

    // ---- CREAR RUTINA (para rutinas personalizadas) ----
    @POST("rutinas/usuario/{idUsuario}")
    suspend fun crearRutina(@Path("idUsuario") idUsuario: Int, @Body rutina: CrearRutinaRequest
    ): Response<Rutina>

    @POST("rutina-ejercicio")
    suspend fun agregarEjercicioARutina(
        @Body request: RutinaEjercicioRequest
    ): Response<RutinaEjercicio>

    // ---- CATÁLOGO EJERCICIOS (para selector) ----
    @GET("ejercicios/activos")
    suspend fun obtenerEjerciciosActivos(): Response<List<Ejercicio>>

    // ---- EDITAR RUTINA ----
    @PUT("rutinas/{idRutina}/usuario/{idUsuario}")
    suspend fun editarRutina(
        @Path("idRutina") idRutina: Int,
        @Path("idUsuario") idUsuario: Int,
        @Body request: CrearRutinaRequest
    ): Response<Rutina>

    // ---- REEMPLAZAR EJERCICIOS DE RUTINA ----
    @PUT("rutina-ejercicio/rutina/{idRutina}/reemplazar")
    suspend fun reemplazarEjerciciosDeRutina(
        @Path("idRutina") idRutina: Int,
        @Body ejercicios: List<RutinaEjercicioRequest>
    ): Response<List<RutinaEjercicio>>

    // ---- COPIAR RUTINA BASE COMO PERSONAL ----
    @POST("rutinas/{idRutinaBase}/copiar/usuario/{idUsuario}")
    suspend fun copiarRutinaBaseAUsuario(
        @Path("idRutinaBase") idRutinaBase: Int,
        @Path("idUsuario") idUsuario: Int
    ): Response<Rutina>

    @DELETE("rutinas/{idRutina}/usuario/{idUsuario}")
    suspend fun eliminarRutinaPersonal(
        @Path("idRutina") idRutina: Int,
        @Path("idUsuario") idUsuario: Int
    ): Response<Unit>

}