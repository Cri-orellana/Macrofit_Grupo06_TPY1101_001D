package com.duoc.macrofit.api

import com.duoc.macrofit.model.LoginRequest
import com.duoc.macrofit.model.NvActividad
import com.duoc.macrofit.model.Objetivo
import com.duoc.macrofit.model.RegistroRequest
import com.duoc.macrofit.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MacrofitApi {
    @POST("usuarios/login")
    suspend fun login(@Body credenciales: LoginRequest): Response<Usuario>


    @GET("catalogos/objetivos")
    suspend fun obtenerObjetivos(): Response<List<Objetivo>>

    @GET("catalogos/actividades")
    suspend fun obtenerActividades(): Response<List<NvActividad>>

    @POST("usuarios/registro")
    suspend fun registrarUsuario(@Body nuevoUsuario: RegistroRequest): Response<Usuario>
}