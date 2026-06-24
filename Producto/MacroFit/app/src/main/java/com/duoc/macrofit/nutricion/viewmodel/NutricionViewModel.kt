package com.duoc.macrofit.nutricion.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.nutricion.model.ComidaRecomendada
import com.duoc.macrofit.nutricion.model.TipoAlimentacion
import com.duoc.macrofit.usuarios.api.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONArray


class NutricionViewModel : ViewModel() {

    var listaTiposDieta by mutableStateOf<List<TipoAlimentacion>>(emptyList())
    var listaComidas by mutableStateOf<List<ComidaRecomendada>>(emptyList())
    var todasLasComidas by mutableStateOf<List<ComidaRecomendada>>(emptyList())
    var dietaSeleccionada by mutableStateOf<TipoAlimentacion?>(null)
    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    init {
        obtenerTiposDieta()
        obtenerTodasLasRecetas()
    }

    private fun obtenerTiposDieta() {
        viewModelScope.launch {
            mensajeError = null
            try {
                listaTiposDieta = RetrofitClient.apiNutricion.obtenerTiposDieta()
            } catch (e: Exception) {
                mensajeError = "Error al conectar con el servidor: ${e.message}"
            }
        }
    }

    private fun obtenerTodasLasRecetas() {
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                val respuesta = RetrofitClient.apiNutricion.obtenerRecetasCache()
                Log.d("NUTRICION", "✅ Recetas cache recibidas: ${respuesta.size}")

                val mapeadas = mapearRecetas(respuesta)
                todasLasComidas = mapeadas
                listaComidas = mapeadas

            } catch (e: Exception) {
                Log.e("NUTRICION", "❌ Error recetas cache: ${e.message}", e)
                mensajeError = "Error al cargar recetas: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    fun seleccionarDietaYBuscarComidas(dieta: TipoAlimentacion) {
        dietaSeleccionada = dieta
        val nombreDieta = dieta.nombre_tipo.lowercase().trim()

        listaComidas = todasLasComidas.filter { receta ->
            val tipoCacheKey = receta.cacheKey
                ?.split("|")
                ?.firstOrNull()
                ?.lowercase()
                ?.trim()

            tipoCacheKey == nombreDieta
        }
    }

    fun limpiarFiltro() {
        dietaSeleccionada = null
        listaComidas = todasLasComidas
    }

    fun buscarRecomendacionesInteligentes(
        dieta: String = "",
        ingredienteBuscado: String = "",
        faltanCarbos: Float = 1000f,
        faltaProtes: Float = 0f,
        faltanGrasas: Float = 1000f
    ) {
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                Log.d("NUTRICION", "🔍 Llamando a /recomendaciones con dieta=$dieta, ingredientes=$ingredienteBuscado, carbos=$faltanCarbos, protes=$faltaProtes, grasas=$faltanGrasas")

                val respuesta = RetrofitClient.apiNutricion.obtenerRecomendaciones(
                    tipoDieta = dieta,
                    ingredientes = ingredienteBuscado,
                    maxCarbohidratos = faltanCarbos,
                    minProteina = faltaProtes,
                    maxGrasa = faltanGrasas
                )

                Log.d("NUTRICION", "✅ Recomendaciones recibidas: ${respuesta.size}")
                listaComidas = mapearRecetas(respuesta)

            } catch (e: Exception) {
                Log.e("NUTRICION", "❌ Error recomendaciones: ${e.message}", e)
                mensajeError = "Error al buscar recetas: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    private fun parsearJsonALista(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            val array = JSONArray(json)
            List(array.length()) { i -> array.getString(i) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mapearRecetas(recetas: List<ComidaRecomendada>): List<ComidaRecomendada> {
        return recetas.map { receta ->
            receta.copy(
                ingredientes_lista = parsearJsonALista(receta.ingredientes_json),
                preparacion_lista  = parsearJsonALista(receta.preparacion_json)
            )
        }
    }


}