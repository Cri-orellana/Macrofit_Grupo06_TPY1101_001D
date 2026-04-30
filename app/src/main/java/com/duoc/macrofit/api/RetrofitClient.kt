package com.duoc.macrofit.api


import com.duoc.macrofit.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val apiService: MacrofitApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            // Aquí entra GSON: convierte los JSON de Spring Boot en las Data Classes que creamos arriba
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MacrofitApi::class.java)
    }
}