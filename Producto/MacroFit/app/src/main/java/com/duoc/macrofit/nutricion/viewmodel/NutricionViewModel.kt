package com.duoc.macrofit.nutricion.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.nutricion.model.ComidaRecomendada
import com.duoc.macrofit.nutricion.model.TipoAlimentacion
import com.duoc.macrofit.usuarios.api.RetrofitClient
import kotlinx.coroutines.launch

class NutricionViewModel : ViewModel() {

    // Variables de estado para la interfaz
    var listaTiposDieta by mutableStateOf<List<TipoAlimentacion>>(emptyList())
    var listaComidas by mutableStateOf<List<ComidaRecomendada>>(emptyList())

    var dietaSeleccionada by mutableStateOf<TipoAlimentacion?>(null)

    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    init {
        // Apenas se abra la pantalla, descargamos los tipos de dieta
        obtenerTiposDieta()
    }

    private fun obtenerTiposDieta() {
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                // Llamada a tu endpoint de Spring Boot
                val respuesta = RetrofitClient.apiNutricion.obtenerTiposDieta()
                listaTiposDieta = respuesta
            } catch (e: Exception) {
                mensajeError = "Error al conectar con el servidor: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    // Esta función la llamaremos cuando el usuario toque una tarjeta de dieta
    fun seleccionarDietaYBuscarComidas(dieta: TipoAlimentacion) {
        dietaSeleccionada = dieta
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                // Buscamos los platos específicos para el ID de esa dieta
                val respuesta = RetrofitClient.apiNutricion.obtenerComidasPorTipo(dieta.id_tipo_alimentacion)
                listaComidas = respuesta
            } catch (e: Exception) {
                mensajeError = "Error al obtener las comidas: ${e.message}"
            } finally {
                cargando = false
            }
        }
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
                // Llamamos al endpoint de Spooncular
                val respuesta = RetrofitClient.apiNutricion.obtenerRecomendaciones(
                    tipoDieta = dieta,
                    ingredientes = ingredienteBuscado,
                    maxCarbohidratos = faltanCarbos,
                    minProteina = faltaProtes,
                    maxGrasa = faltanGrasas
                )
                // Actualizamos la lista de la pantalla con las 5 recetas nuevas
                listaComidas = respuesta
            } catch (e: Exception) {
                mensajeError = "Error al buscar recetas: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }
}