package com.duoc.macrofit.rutinas.api

import com.duoc.macrofit.usuarios.utils.Constants
import com.duoc.macrofit.usuarios.utils.HttpClientProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitRutinas {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(HttpClientProvider.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: RutinaEjercicioApi by lazy {
        retrofit.create(RutinaEjercicioApi::class.java)
    }
}