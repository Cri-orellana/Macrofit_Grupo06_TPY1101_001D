package com.duoc.macrofit.macros.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.macros.api.RetrofitClient
import com.duoc.macrofit.macros.model.ComidaDto
import com.duoc.macrofit.macros.model.ComidaNuevaRequest
import com.duoc.macrofit.macros.model.ComidaResponse
import com.duoc.macrofit.macros.model.Nutriments
import com.duoc.macrofit.usuarios.model.Usuario
import com.duoc.macrofit.usuarios.utils.SessionManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ComidaAgregada(
    val id: Long,
    val nombre: String,
    val porcion: Double,
    val calorias: Double,
    val proteinas: Double,
    val carbohidratos: Double,
    val grasas: Double,
    val fecha: String? = null
)

class SeleccionarComidaViewModel : ViewModel() {

    private val gson = Gson()
    private val _comidasAgregadas = MutableStateFlow<List<ComidaAgregada>>(emptyList())
    val comidasAgregadas: StateFlow<List<ComidaAgregada>> = _comidasAgregadas.asStateFlow()

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

    private val _comidaEscaneada = MutableStateFlow<ComidaDto?>(null)
    val comidaEscaneada: StateFlow<ComidaDto?> = _comidaEscaneada.asStateFlow()

    // Límites de Macronutrientes según objetivo
    private val _limiteCalorias = MutableStateFlow(2000.0)
    val limiteCalorias: StateFlow<Double> = _limiteCalorias.asStateFlow()

    private val _limiteProteinas = MutableStateFlow(0.0)
    val limiteProteinas: StateFlow<Double> = _limiteProteinas.asStateFlow()

    private val _limiteCarbohidratos = MutableStateFlow(0.0)
    val limiteCarbohidratos: StateFlow<Double> = _limiteCarbohidratos.asStateFlow()

    private val _limiteGrasas = MutableStateFlow(0.0)
    val limiteGrasas: StateFlow<Double> = _limiteGrasas.asStateFlow()

    init {
        cargarCache()
        calcularLimitesMacros()
    }

    private fun calcularLimitesMacros() {
        val usuario = SessionManager.usuarioActual ?: return
        val totalCal = usuario.cal_diaria?.toDouble() ?: 2000.0
        _limiteCalorias.value = totalCal

        // 1. Perder grasa (P 32.5%, C 30%, G 40%) - Usamos promedios de tus rangos
        // 2. Mantener (P 25%, C 47.5%, G 27.5%)
        // 3. Ganar masa (P 30%, C 45%, G 25%)
        
        when (usuario.id_objetivo) {
            1 -> { // Perder Grasa
                _limiteProteinas.value = (totalCal * 0.325) / 4
                _limiteCarbohidratos.value = (totalCal * 0.30) / 4
                _limiteGrasas.value = (totalCal * 0.40) / 9
            }
            2 -> { // Mantener
                _limiteProteinas.value = (totalCal * 0.25) / 4
                _limiteCarbohidratos.value = (totalCal * 0.475) / 4
                _limiteGrasas.value = (totalCal * 0.275) / 9
            }
            3 -> { // Ganar Masa
                _limiteProteinas.value = (totalCal * 0.30) / 4
                _limiteCarbohidratos.value = (totalCal * 0.45) / 4
                _limiteGrasas.value = (totalCal * 0.25) / 9
            }
            else -> {
                // Default balanceado si no hay objetivo
                _limiteProteinas.value = (totalCal * 0.25) / 4
                _limiteCarbohidratos.value = (totalCal * 0.50) / 4
                _limiteGrasas.value = (totalCal * 0.25) / 9
            }
        }
    }

    private fun cargarCache() {
        val cache = SessionManager.obtenerDiarioCache()
        if (cache.isNotEmpty()) {
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
                val resultadosFinales = mutableListOf<ComidaDto>()

                val locales = SessionManager.obtenerProductosCacheLocal().values.filter {
                    it.nombre?.contains(nombre, ignoreCase = true) == true
                }
                resultadosFinales.addAll(locales)

                val respPropia = RetrofitClient.apiMacros.buscarComidaPropia(nombre)
                if (respPropia.isSuccessful) {
                    respPropia.body()?.forEach { cr ->
                        resultadosFinales.add(
                            ComidaDto(
                                barra = cr.code,
                                nombre = cr.nombre,
                                nutriments = Nutriments(
                                    calorias = cr.calorias,
                                    carbohidratos = cr.carbohidratos,
                                    proteinas = cr.proteinas,
                                    grasas = cr.grasas
                                )
                            )
                        )
                    }
                }

                val respOpen = RetrofitClient.apiMacros.buscarComidaOpen(nombre)
                if (respOpen.isSuccessful) {
                    respOpen.body()?.let { listaOpen ->
                        resultadosFinales.addAll(listaOpen)
                    }
                }

                _resultadosBusqueda.value = resultadosFinales
                    .distinctBy { it.barra ?: it.nombre?.lowercase() }

            } catch (e: Exception) {
                Log.e("BUSCAR", "Error en búsqueda: ${e.message}", e)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun buscarPorCodigoDeBarra(codigo: String, onResult: (ComidaDto?) -> Unit) {
        val productosLocales = SessionManager.obtenerProductosCacheLocal()
        if (productosLocales.containsKey(codigo)) {
            val local = productosLocales[codigo]
            _comidaEscaneada.value = local
            onResult(local)
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            try {
                val respPropia = RetrofitClient.apiMacros.buscarComidaBarraPropia(codigo)
                if (respPropia.isSuccessful && respPropia.body() != null) {
                    val cr = respPropia.body()!!
                    val comida = ComidaDto(
                        barra = cr.code,
                        nombre = cr.nombre,
                        nutriments = Nutriments(
                            calorias = cr.calorias,
                            carbohidratos = cr.carbohidratos,
                            proteinas = cr.proteinas,
                            grasas = cr.grasas
                        )
                    )
                    SessionManager.guardarProductoEnCacheLocal(comida)
                    _comidaEscaneada.value = comida
                    onResult(comida)
                    return@launch
                }

                val responseOpen = RetrofitClient.apiMacros.buscarComidaBarraOpen(codigo)
                if (responseOpen.isSuccessful && responseOpen.body() != null) {
                    val comida = responseOpen.body()!!
                    SessionManager.guardarProductoEnCacheLocal(comida)
                    _comidaEscaneada.value = comida
                    onResult(comida)
                } else {
                    onResult(null)
                }
            } catch (e: Exception) {
                Log.e("BARCODE", "Error: ${e.message}")
                onResult(null)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun resetComidaEscaneada() {
        _comidaEscaneada.value = null
    }

    fun cargarDiarioDelDia(usuarioId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiMacros.obtenerDiario(usuarioId.toString())
                if (response.isSuccessful && response.body() != null) {
                    val items = response.body()!!
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val hoy = sdf.format(Date())

                    val hayItemsAntiguos = items.any { 
                        it.fechareg != null && it.fechareg.length >= 10 && !it.fechareg.startsWith(hoy) 
                    }

                    if (hayItemsAntiguos) {
                        Log.d("DIARIO_AUTO", "Se detectaron alimentos de días anteriores. Cerrando día...")
                        val closeResp = RetrofitClient.apiMacros.cerrarDiaManual(usuarioId.toString())
                        if (closeResp.isSuccessful) {
                            _comidasAgregadas.value = emptyList()
                            recalcularTotales(emptyList())
                            return@launch
                        }
                    }

                    val listaData = items.map { comidaProc ->
                        ComidaAgregada(
                            id = comidaProc.id,
                            nombre = comidaProc.nombre,
                            porcion = comidaProc.porcion,
                            calorias = comidaProc.calorias,
                            proteinas = comidaProc.proteinas,
                            carbohidratos = comidaProc.carbohidratos,
                            grasas = comidaProc.grasas,
                            fecha = comidaProc.fechareg
                        )
                    }
                    _comidasAgregadas.value = listaData
                    recalcularTotales(listaData)
                }
            } catch (e: Exception) {
                Log.e("DIARIO", "Error cargando diario: ${e.message}")
            }
        }
    }

    fun agregarAlimento(comidaDto: ComidaDto, gramos: Double, usuarioId: Int) {
        viewModelScope.launch {
            try {
                _cargando.value = true

                if (comidaDto.barra != null) {
                    try {
                        val request = ComidaNuevaRequest(
                            code = comidaDto.barra,
                            nombre = comidaDto.nombre,
                            calorias = comidaDto.nutriments?.calorias,
                            proteinas = comidaDto.nutriments?.proteinas,
                            carbohidratos = comidaDto.nutriments?.carbohidratos,
                            grasas = comidaDto.nutriments?.grasas
                        )
                        RetrofitClient.apiMacros.guardarComidaNueva(request)
                        SessionManager.guardarProductoEnCacheLocal(comidaDto)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

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
                        grasas = comidaProc.grasas,
                        fecha = comidaProc.fechareg
                    )
                    val listaActualizada = _comidasAgregadas.value + nuevaComida
                    _comidasAgregadas.value = listaActualizada
                    recalcularTotales(listaActualizada)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _cargando.value = false
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
                                grasas = comidaProc.grasas,
                                fecha = comidaProc.fechareg
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

    fun cerrarDia(usuarioId: Int) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                val response = RetrofitClient.apiMacros.cerrarDiaManual(usuarioId.toString())
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
                            grasas = it.grasas,
                            fecha = it.fechareg
                        )
                    }.sortedByDescending { it.id }
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

    fun agregarRecetaRecomendada(
        nombre: String,
        calorias: Double,
        proteinas: Double,
        carbohidratos: Double,
        grasas: Double,
        usuarioId: Int
    ) {
        viewModelScope.launch {
            try {
                _cargando.value = true

                val comidaDto = ComidaDto(
                    barra = null,
                    nombre = nombre,
                    nutriments = Nutriments(
                        calorias = calorias,
                        proteinas = proteinas,
                        carbohidratos = carbohidratos,
                        grasas = grasas
                    )
                )

                val response = RetrofitClient.apiMacros.agregarAlDiario(comidaDto, 100.0, usuarioId.toString())

                if (response.isSuccessful && response.body() != null) {
                    val comidaProc = response.body()!!
                    val nuevaComida = ComidaAgregada(
                        id = comidaProc.id,
                        nombre = comidaProc.nombre,
                        porcion = comidaProc.porcion,
                        calorias = comidaProc.calorias,
                        proteinas = comidaProc.proteinas,
                        carbohidratos = comidaProc.carbohidratos,
                        grasas = comidaProc.grasas,
                        fecha = comidaProc.fechareg
                    )
                    val listaActualizada = _comidasAgregadas.value + nuevaComida
                    _comidasAgregadas.value = listaActualizada
                    recalcularTotales(listaActualizada)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _cargando.value = false
            }
        }
    }
}
