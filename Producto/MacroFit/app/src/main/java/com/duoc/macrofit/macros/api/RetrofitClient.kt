package com.duoc.macrofit.macros.api

import android.util.Log
import com.duoc.macrofit.usuarios.utils.Constants
import com.duoc.macrofit.usuarios.utils.HttpClientProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    val apiMacros: ApiMacros by lazy {
        try {
            Log.d("MACROFIT_DEBUG", "Construyendo Retrofit macros con: " + Constants.BASE_URL)

            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(HttpClientProvider.client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiMacros::class.java)
        } catch (e: Exception) {
            Log.e("MACROFIT_DEBUG", "¡FALLO CRÍTICO EN RETROFIT MACROS!", e)
            throw e
        }
    }
}
