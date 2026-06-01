package com.duoc.macrofit.macros.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.macros.api.RetrofitClient
import com.duoc.macrofit.macros.model.ComidaDto
import com.duoc.macrofit.macros.model.ComidaResponse
import com.duoc.macrofit.usuarios.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    private val gson = Gson()
    private val _comidasAgregadas = MutableStateFlow<List<ComidaAgregada>>(emptyList())
    val comidasAgregadas: StateFlow<List<ComidaAgregada>> = _comidasAgregadas.asStateFlow()

    // ... (rest of states)
    private val _resultadosBusqueda = MutableStateFlow<List<ComidaDto>>(emptyList())
    val resultadosBusqueda: StateFlow<List<ComidaDto>> = _resultadosBusqueda.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _totalCalorias = MutableStateFlow(0.0)
    val totalCalorias: StateFlow<Double> = _totalCalorias.asStateFlow()

    private val _totalProteinas = MutableStateFlow(0.0)
    val totalProteinas: StateFlow<Double> = _totalProteinas.asStateFlow()

    private val _totalCarbohidratos = MutableStateFlow(0.0)
    val totalCarbohidratos: StateFlow<Double> = _totalCarbohidratos.asStateFlow()

    private val _totalGrasas = MutableStateFlow(0.0)
    val totalGrasas: StateFlow<Double> = _totalGrasas.asStateFlow()

    private val _historial = MutableStateFlow<List<ComidaAgregada>>(emptyList())
    val historial: StateFlow<List<ComidaAgregada>> = _historial.asStateFlow()

    init {
        cargarCache()
    }

    private fun cargarCache() {
        val cache = SessionManager.obtenerDiarioCache()
        if (cache != null) {
            try {
                val type = object : TypeToken<List<ComidaAgregada>>() {}.type
                val lista: List<ComidaAgregada> = gson.fromJson(cache, type)
                _comidasAgregadas.value = lista
                recalcularTotales(lista)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun guardarEnCache(lista: List<ComidaAgregada>) {
        val json = gson.toJson(lista)
        SessionManager.guardarDiarioCache(json)
    }

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

    fun agregarAlimento(comidaDto: ComidaDto, gramos: Double, usuarioId: Int) {
        viewModelScope.launch {
            try {
                // Enviamos el DTO directamente (ya tiene los nutriments anidados)
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

    fun eliminarAlimento(id: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiMacros.eliminarComida(id)
                if (response.isSuccessful) {
                    val listaActualizada = _comidasAgregadas.value.filter { it.id != id }
                    _comidasAgregadas.value = listaActualizada
                    recalcularTotales(listaActualizada)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun actualizarPorcion(id: Long, nuevosGramos: Double) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiMacros.actualizarComida(id, nuevosGramos)
                if (response.isSuccessful && response.body() != null) {
                    val comidaProc = response.body()!!
                    val listaActualizada = _comidasAgregadas.value.map {
                        if (it.id == id) {
                            ComidaAgregada(
                                id = comidaProc.id,
                                porcion = comidaProc.porcion,
                                nombre = comidaProc.nombre,
                                calorias = comidaProc.calorias,
                                proteinas = comidaProc.proteinas,
                                carbohidratos = comidaProc.carbohidratos,
                                grasas = comidaProc.grasas
                            )
                        } else it
                    }
                    _comidasAgregadas.value = listaActualizada
                    recalcularTotales(listaActualizada)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun cerrarDia() {
        viewModelScope.launch {
            try {
                _cargando.value = true
                val response = RetrofitClient.apiMacros.cerrarDiaManual()
                if (response.isSuccessful) {
                    _comidasAgregadas.value = emptyList()
                    recalcularTotales(emptyList())
                    guardarEnCache(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarHistorial(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiMacros.obtenerHistorial(usuarioId.toString())
                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()!!.map {
                        ComidaAgregada(
                            id = it.id,
                            nombre = it.nombre,
                            porcion = it.porcion,
                            calorias = it.calorias,
                            proteinas = it.proteinas,
                            carbohidratos = it.carbohidratos,
                            grasas = it.grasas
                        )
                    }
                    _historial.value = lista
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
        guardarEnCache(lista)
    }
}
