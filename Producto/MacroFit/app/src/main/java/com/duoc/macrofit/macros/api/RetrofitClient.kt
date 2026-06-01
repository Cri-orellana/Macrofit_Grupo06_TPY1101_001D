package com.duoc.macrofit.macros.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // CORRECCIÓN: La URL base solo debe llegar hasta /api/
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val apiMacros: ApiMacros by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiMacros::class.java)
    }
}