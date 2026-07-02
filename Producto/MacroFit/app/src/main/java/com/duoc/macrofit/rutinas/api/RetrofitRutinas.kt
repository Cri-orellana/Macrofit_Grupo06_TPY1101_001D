package com.duoc.macrofit.rutinas.api

import com.duoc.macrofit.usuarios.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitRutinas {

    private const val BASE_URL = "http://165.1.126.244/"

    // 1. Creamos el interceptor que inyectará el Token en CADA petición de Rutinas
    private val authInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()

        // Obtenemos el token desde tu SessionManager
        val token = SessionManager.obtenerToken()

        // Si hay token, lo metemos en los Headers (como "Bearer <token>")
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // Continuamos con la petición ya modificada
        chain.proceed(requestBuilder.build())
    }

    // 2. Configuramos OkHttp para que use el interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        // Opcional: Agregar timeouts para que no se quede colgado si la red está lenta
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 3. Le pasamos el cliente configurado a Retrofit
    val apiService: RutinaEjercicioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <-- Esta línea es la magia
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RutinaEjercicioApi::class.java)
    }
}