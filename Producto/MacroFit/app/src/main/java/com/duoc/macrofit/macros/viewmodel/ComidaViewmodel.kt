package com.duoc.macrofit.macros.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.macros.api.RetrofitClient
import com.duoc.macrofit.macros.model.ComidaDto
import com.duoc.macrofit.macros.model.ComidaResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ComidaAgregada(
    val id: Long,
    val nombre: String,
    val porcion: Double,
    val calorias: Double,
    val proteinas: Double,
    val carbohidratos: Double,
    val grasas: Double
)

class SeleccionarComidaViewModel : ViewModel() {

    private val _comidasAgregadas = MutableStateFlow<List<ComidaAgregada>>(emptyList())
    val comidasAgregadas: StateFlow<List<ComidaAgregada>> = _comidasAgregadas.asStateFlow()

    private val _resultadosBusqueda = MutableStateFlow<List<ComidaResponse>>(emptyList())
    val resultadosBusqueda: StateFlow<List<ComidaResponse>> = _resultadosBusqueda.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Totales de macros
    private val _totalCalorias = MutableStateFlow(0.0)
    val totalCalorias: StateFlow<Double> = _totalCalorias.asStateFlow()

    private val _totalProteinas = MutableStateFlow(0.0)
    val totalProteinas: StateFlow<Double> = _totalProteinas.asStateFlow()

    private val _totalCarbohidratos = MutableStateFlow(0.0)
    val totalCarbohidratos: StateFlow<Double> = _totalCarbohidratos.asStateFlow()

    private val _totalGrasas = MutableStateFlow(0.0)
    val totalGrasas: StateFlow<Double> = _totalGrasas.asStateFlow()

    fun buscarComida(nombre: String) {
        if (nombre.isBlank()) return
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = RetrofitClient.apiMacros.buscarComidaNombre(nombre)
                if (response.isSuccessful) {
                    _resultadosBusqueda.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarDiarioDelDia(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiMacros.obtenerDiario(usuarioId.toString())
                if (response.isSuccessful && response.body() != null) {
                    val listaData = response.body()!!.map { comidaProc ->
                        ComidaAgregada(
                            id = comidaProc.id,
                            nombre = comidaProc.nombre,
                            porcion = comidaProc.porcion,
                            calorias = comidaProc.calorias,
                            proteinas = comidaProc.proteinas,
                            carbohidratos = comidaProc.carbohidratos,
                            grasas = comidaProc.grasas
                        )
                    }
                    _comidasAgregadas.value = listaData
                    recalcularTotales(listaData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun agregarAlimento(comidaResponse: ComidaResponse, gramos: Double, usuarioId: Int) {
        viewModelScope.launch {
            try {
                // Convertimos ComidaResponse a ComidaDto para el POST
                val comidaDto = ComidaDto(
                    barra = comidaResponse.code,
                    nombre = comidaResponse.nombre,
                    calorias = comidaResponse.calorias,
                    carbohidratos = comidaResponse.carbohidratos,
                    proteinas = comidaResponse.proteinas,
                    grasas = comidaResponse.grasas
                )

                val response = RetrofitClient.apiMacros.agregarAlDiario(comidaDto, gramos, usuarioId.toString())

                if (response.isSuccessful && response.body() != null) {
                    val comidaProc = response.body()!!
                    val nuevaComida = ComidaAgregada(
                        id = comidaProc.id,
                        porcion = comidaProc.porcion,
                        nombre = comidaProc.nombre,
                        calorias = comidaProc.calorias,
                        proteinas = comidaProc.proteinas,
                        carbohidratos = comidaProc.carbohidratos,
                        grasas = comidaProc.grasas
                    )
                    val listaActualizada = _comidasAgregadas.value + nuevaComida
                    _comidasAgregadas.value = listaActualizada
                    recalcularTotales(listaActualizada)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun recalcularTotales(lista: List<ComidaAgregada>) {
        _totalCalorias.value = lista.sumOf { it.calorias }
        _totalProteinas.value = lista.sumOf { it.proteinas }
        _totalCarbohidratos.value = lista.sumOf { it.carbohidratos }
        _totalGrasas.value = lista.sumOf { it.grasas }
    }
}
