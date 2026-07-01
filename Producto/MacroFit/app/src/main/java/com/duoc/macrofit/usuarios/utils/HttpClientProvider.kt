package com.duoc.macrofit.usuarios.utils

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object HttpClientProvider {

    val client: OkHttpClient by lazy {
        // 1. Crear el interceptor de logs
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Esto imprimirá TODO: URL, Headers y Body
        }

        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logging) // 2. Agregar el logger a OkHttp
            .addInterceptor { chain ->
                val original = chain.request()
                val token = SessionManager.obtenerToken()

                val request = if (!token.isNullOrEmpty()) {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else {
                    original
                }
                chain.proceed(request)
            }
            .build()
    }
}