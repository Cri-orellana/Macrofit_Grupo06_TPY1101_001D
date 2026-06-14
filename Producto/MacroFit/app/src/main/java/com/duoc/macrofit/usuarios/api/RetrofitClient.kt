package com.duoc.macrofit.usuarios.api

import com.duoc.macrofit.nutricion.api.ApiNutricion
import com.duoc.macrofit.usuarios.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val clienteHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private val retrofitBase: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(clienteHttp)
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