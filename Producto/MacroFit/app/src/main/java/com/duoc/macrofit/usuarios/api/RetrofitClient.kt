package com.duoc.macrofit.usuarios.api

import com.duoc.macrofit.nutricion.api.ApiNutricion
import com.duoc.macrofit.usuarios.utils.Constants
import com.duoc.macrofit.usuarios.utils.HttpClientProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofitBase: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(HttpClientProvider.client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: MacrofitApi by lazy {
        retrofitBase.create(MacrofitApi::class.java)
    }

    val apiNutricion: ApiNutricion by lazy {
        retrofitBase.create(ApiNutricion::class.java)
    }
}