package com.duoc.macrofit.macros.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.macros.api.RetrofitClient
import com.duoc.macrofit.usuarios.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class EstadisticaDiaria(
    val fecha: String,
    val calorias: Double,
    val proteinas: Double,
    val carbohidratos: Double,
    val grasas: Double
)

class ProgresoViewModel : ViewModel() {

    var listaEstadisticas by mutableStateOf<List<EstadisticaDiaria>>(emptyList())
    var listaFiltrada by mutableStateOf<List<EstadisticaDiaria>>(emptyList())
    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    // Filtros actuales
    var periodoSeleccionado by mutableStateOf("Semana") // "Semana", "Mes"
    var macroSeleccionado by mutableStateOf("Calorías") // "Calorías", "Proteínas", "Carbos", "Grasas"

    // Límites de referencia para el eje Y
    var limiteCalorias by mutableStateOf(2000.0)
    var limiteProteinas by mutableStateOf(150.0)
    var limiteCarbos by mutableStateOf(250.0)
    var limiteGrasas by mutableStateOf(70.0)

    fun cargarDatosHistoricos() {
        val usuarioId = SessionManager.usuarioActual?.id ?: return
        val usuario = SessionManager.usuarioActual!!

        // Calcular límites basados en el perfil del usuario (mismo cálculo que ComidaViewModel)
        val totalCal = usuario.cal_diaria?.toDouble() ?: 2000.0
        limiteCalorias = totalCal
        when (usuario.id_objetivo) {
            1 -> { // Perder Grasa
                limiteProteinas = (totalCal * 0.325) / 4
                limiteCarbos = (totalCal * 0.30) / 4
                limiteGrasas = (totalCal * 0.40) / 9
            }
            2 -> { // Mantener
                limiteProteinas = (totalCal * 0.25) / 4
                limiteCarbos = (totalCal * 0.475) / 4
                limiteGrasas = (totalCal * 0.275) / 9
            }
            3 -> { // Ganar Masa
                limiteProteinas = (totalCal * 0.30) / 4
                limiteCarbos = (totalCal * 0.45) / 4
                limiteGrasas = (totalCal * 0.25) / 9
            }
            else -> {
                limiteProteinas = (totalCal * 0.25) / 4
                limiteCarbos = (totalCal * 0.50) / 4
                limiteGrasas = (totalCal * 0.25) / 9
            }
        }
        
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                val response = RetrofitClient.apiMacros.obtenerHistorial(usuarioId.toString())
                if (response.isSuccessful && response.body() != null) {
                    val historial = response.body()!!
                    
                    val agrupado = historial.groupBy { 
                        it.fechareg?.substringBefore("T") ?: "Sin fecha"
                    }

                    listaEstadisticas = agrupado.map { (fecha, comidas) ->
                        EstadisticaDiaria(
                            fecha = fecha,
                            calorias = comidas.sumOf { it.calorias },
                            proteinas = comidas.sumOf { it.proteinas },
                            carbohidratos = comidas.sumOf { it.carbohidratos },
                            grasas = comidas.sumOf { it.grasas }
                        )
                    }.sortedBy { it.fecha }

                    actualizarFiltros()

                } else {
                    mensajeError = "No se pudieron obtener los datos históricos"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    fun cambiarPeriodo(nuevoPeriodo: String) {
        periodoSeleccionado = nuevoPeriodo
        actualizarFiltros()
    }

    fun cambiarMacro(nuevoMacro: String) {
        macroSeleccionado = nuevoMacro
    }

    private fun actualizarFiltros() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val hoy = Calendar.getInstance()
        
        listaFiltrada = when (periodoSeleccionado) {
            "Semana" -> {
                // Obtener los 7 días de la semana actual (Lunes a Domingo)
                val semanaActual = mutableListOf<EstadisticaDiaria>()
                val cal = Calendar.getInstance().apply {
                    firstDayOfWeek = Calendar.MONDAY
                    set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                }
                
                for (i in 0..6) {
                    val fechaStr = sdf.format(cal.time)
                    val dataExistente = listaEstadisticas.find { it.fecha == fechaStr }
                    semanaActual.add(dataExistente ?: EstadisticaDiaria(fechaStr, 0.0, 0.0, 0.0, 0.0))
                    cal.add(Calendar.DATE, 1)
                }
                semanaActual
            }
            "Mes" -> {
                // Obtener todos los días del mes actual
                val mesActual = mutableListOf<EstadisticaDiaria>()
                val cal = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                }
                val maxDias = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                
                for (i in 1..maxDias) {
                    val fechaStr = sdf.format(cal.time)
                    val dataExistente = listaEstadisticas.find { it.fecha == fechaStr }
                    mesActual.add(dataExistente ?: EstadisticaDiaria(fechaStr, 0.0, 0.0, 0.0, 0.0))
                    cal.add(Calendar.DATE, 1)
                }
                mesActual
            }
            else -> listaEstadisticas
        }
    }
}
