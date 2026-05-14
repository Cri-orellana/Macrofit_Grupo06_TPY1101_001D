package com.duoc.macrofit.usuarios.api


import com.duoc.macrofit.usuarios.model.LoginRequest
import com.duoc.macrofit.usuarios.model.NvActividad
import com.duoc.macrofit.usuarios.model.Objetivo
import com.duoc.macrofit.usuarios.model.RegistroRequest
import com.duoc.macrofit.usuarios.model.Usuario
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