package com.duoc.macrofit.macros.api

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.duoc.macrofit.usuarios.utils.Constants

object RetrofitClient {

    val apiMacros: ApiMacros by lazy {
        try {
            Log.d("MACROFIT_DEBUG", "Intentando construir Retrofit con: " + Constants.BASE_URL)

            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiMacros::class.java)
        } catch (e: Exception) {
            Log.e("MACROFIT_DEBUG", "¡FALLO CRÍTICO EN RETROFIT!", e)
            throw e
        }
    }
}