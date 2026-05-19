package com.duoc.macrofit.rutinas.api

import com.duoc.macrofit.rutinas.model.AsignarRutinaRequest
import com.duoc.macrofit.rutinas.model.Ejercicio
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.rutinas.model.RutinaEjercicio
import com.duoc.macrofit.rutinas.model.RutinaUsuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface RutinaEjercicioApi {

    // ---- RUTINAS ----
    @GET("rutinas/activas")
    suspend fun obtenerRutinasActivas(): Response<List<Rutina>>

    @GET("rutinas/{id}")
    suspend fun obtenerRutinaPorId(@Path("id") id: Int): Response<Rutina>

    // ---- EJERCICIOS ----
    @GET("ejercicios/{id}")
    suspend fun obtenerEjercicioPorId(@Path("id") id: Int): Response<Ejercicio>

    // ---- RUTINA-EJERCICIO ----
    @GET("rutina-ejercicio/rutina/{id_rutina}")
    suspend fun obtenerEjerciciosDeRutina(
        @Path("id_rutina") idRutina: Int
    ): Response<List<RutinaEjercicio>>

    // ---- RUTINA-USUARIO ----
    @GET("rutina-usuario/usuario/{id_usuario}/activa")
    suspend fun obtenerRutinaActiva(
        @Path("id_usuario") idUsuario: Int
    ): Response<RutinaUsuario>

    @POST("rutina-usuario")
    suspend fun asignarRutina(
        @Body request: AsignarRutinaRequest
    ): Response<RutinaUsuario>

    @PATCH("rutina-usuario/{id}/desactivar")
    suspend fun desactivarAsignacion(
        @Path("id") idAsignacion: Int
    ): Response<RutinaUsuario>
}