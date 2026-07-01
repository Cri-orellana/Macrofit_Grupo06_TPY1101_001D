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
import com.duoc.macrofit.utils.TraduccionUtils
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
        Log.d("TRADUCCION_DEBUG", "NutricionViewModel INICIADO")
        obtenerTiposDieta()
        obtenerTodasLasRecetas()
    }

    private fun obtenerTiposDieta() {
        viewModelScope.launch {
            mensajeError = null
            try {
                // Obtenemos los tipos de dieta y los traducimos para la visualización en la UI
                // Pero guardaremos el nombre original para el filtrado si es necesario
                val dietas = RetrofitClient.apiNutricion.obtenerTiposDieta()
                listaTiposDieta = dietas.map { it.copy(nombre_tipo = TraduccionUtils.traducir(it.nombre_tipo)) }
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
                Log.d("TRADUCCION_DEBUG", "✅ Crudo: Recetas cache recibidas: ${respuesta.size}")
                respuesta.forEach { 
                    Log.d("TRADUCCION_DEBUG", "Dato crudo del servidor: '${it.nombre_comida}'")
                }

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
        // Para filtrar, traducimos el nombre de vuelta al inglés si es necesario, 
        // o usamos el nombre que ya viene (que ahora está traducido en la lista)
        val nombreDietaIngles = TraduccionUtils.traducirAlIngles(dieta.nombre_tipo)?.lowercase()?.trim() ?: ""

        listaComidas = todasLasComidas.filter { receta ->
            val tipoCacheKey = receta.cacheKey
                ?.split("|")
                ?.firstOrNull()
                ?.lowercase()
                ?.trim()

            tipoCacheKey == nombreDietaIngles
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
                // Traducir el ingrediente y la dieta buscada al inglés para la API
                val ingredienteEnIngles = TraduccionUtils.traducirAlIngles(ingredienteBuscado)
                val dietaEnIngles = TraduccionUtils.traducirAlIngles(dieta)
                
                Log.d("NUTRICION", "🔍 Buscando: $ingredienteBuscado -> API: $ingredienteEnIngles")

                val respuesta = RetrofitClient.apiNutricion.obtenerRecomendaciones(
                    tipoDieta = dietaEnIngles,
                    ingredientes = ingredienteEnIngles,
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
        Log.d("TRADUCCION_DEBUG", "Mapeando ${recetas.size} recetas para traducción")
        return recetas.map { receta ->
            val ingredientes = parsearJsonALista(receta.ingredientes_json)
            
            val traducida = receta.copy(
                nombre_comida = TraduccionUtils.traducir(receta.nombre_comida),
                descripcion_comida = TraduccionUtils.traducir(receta.descripcion_comida),
                ingredientes_lista = TraduccionUtils.traducirLista(ingredientes),
                preparacion_lista  = parsearJsonALista(receta.preparacion_json)
            )
            Log.d("TRADUCCION_DEBUG", "Receta traducida: ${traducida.nombre_comida}")
            traducida
        }
    }


}