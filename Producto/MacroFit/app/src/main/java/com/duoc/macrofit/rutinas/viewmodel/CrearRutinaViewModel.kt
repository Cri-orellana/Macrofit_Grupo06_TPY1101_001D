package com.duoc.macrofit.rutinas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.rutinas.api.RetrofitRutinas
import com.duoc.macrofit.rutinas.model.CrearRutinaRequest
import com.duoc.macrofit.rutinas.model.Ejercicio
import com.duoc.macrofit.rutinas.model.RutinaEjercicioRequest
import kotlinx.coroutines.launch

/**
 * Representa un ejercicio seleccionado para la rutina que se está creando,
 * junto con sus parámetros editables.
 */
data class EjercicioSeleccionado(
    val idEjercicio: Int,
    val nombreEjercicio: String,
    val series: Int? = 3,
    val repeticiones: Int? = 10,
    val tiempoSeg: Int? = null,
    val pesoReferencia: Float? = null
)

class CrearRutinaViewModel : ViewModel() {

    private val api = RetrofitRutinas.apiService

    // ── Estado del formulario ─────────────────────────────────────────────────
    var nombreRutina by mutableStateOf("")
    var descripcionRutina by mutableStateOf("")

    // ── Catálogo de ejercicios ────────────────────────────────────────────────
    var todosLosEjercicios by mutableStateOf<List<Ejercicio>>(emptyList())
    var cargandoEjercicios by mutableStateOf(false)

    // ── Filtros ───────────────────────────────────────────────────────────────
    var busqueda by mutableStateOf("")

    // zonaFiltro ahora es String? ya que zonaMuscular viene como texto en Ejercicio
    var zonaFiltro by mutableStateOf<String?>(null)

    // Zonas únicas extraídas de la lista de ejercicios (sin llamada extra al backend)
    val zonasDisponibles: List<String>
        get() = todosLosEjercicios
            .mapNotNull { it.zonaMuscular }
            .distinct()
            .sorted()

    // Ejercicios visibles tras aplicar filtros
    val ejerciciosFiltrados: List<Ejercicio>
        get() = todosLosEjercicios.filter { ej ->
            val coincideNombre = busqueda.isBlank() ||
                    ej.nombreEjercicio.contains(busqueda, ignoreCase = true)
            val coincideZona = zonaFiltro == null ||
                    ej.zonaMuscular.equals(zonaFiltro, ignoreCase = true)
            coincideNombre && coincideZona
        }

    // ── Ejercicios seleccionados ──────────────────────────────────────────────
    var ejerciciosSeleccionados by mutableStateOf<List<EjercicioSeleccionado>>(emptyList())

    fun estaSeleccionado(idEjercicio: Int) =
        ejerciciosSeleccionados.any { it.idEjercicio == idEjercicio }

    fun toggleEjercicio(ejercicio: Ejercicio) {
        ejerciciosSeleccionados = if (estaSeleccionado(ejercicio.idEjercicio)) {
            ejerciciosSeleccionados.filter { it.idEjercicio != ejercicio.idEjercicio }
        } else {
            ejerciciosSeleccionados + EjercicioSeleccionado(
                idEjercicio = ejercicio.idEjercicio,
                nombreEjercicio = ejercicio.nombreEjercicio
            )
        }
    }

    fun actualizarParametros(actualizado: EjercicioSeleccionado) {
        ejerciciosSeleccionados = ejerciciosSeleccionados.map {
            if (it.idEjercicio == actualizado.idEjercicio) actualizado else it
        }
    }

    fun eliminarEjercicio(idEjercicio: Int) {
        ejerciciosSeleccionados = ejerciciosSeleccionados.filter { it.idEjercicio != idEjercicio }
    }

    // ── Control de pasos ──────────────────────────────────────────────────────
    var pasoActual by mutableStateOf(1)

    fun avanzarPaso() {
        errorMensaje = null
        when (pasoActual) {
            1 -> {
                if (nombreRutina.isBlank()) {
                    errorMensaje = "Ingresa un nombre para la rutina."
                } else {
                    pasoActual = 2
                    if (todosLosEjercicios.isEmpty()) cargarEjercicios()
                }
            }
            2 -> {
                if (ejerciciosSeleccionados.isEmpty()) {
                    errorMensaje = "Agrega al menos un ejercicio."
                } else {
                    pasoActual = 3
                }
            }
        }
    }

    fun retrocederPaso() {
        errorMensaje = null
        if (pasoActual > 1) pasoActual--
    }

    // ── Carga del catálogo ────────────────────────────────────────────────────
    // Ya no se llama a /zonas — las zonas se extraen de los mismos ejercicios
    private fun cargarEjercicios() {
        viewModelScope.launch {
            cargandoEjercicios = true
            try {
                val resp = api.obtenerEjerciciosActivos()
                if (resp.isSuccessful) todosLosEjercicios = resp.body() ?: emptyList()
                else errorMensaje = "Error al cargar ejercicios del servidor."
            } catch (e: Exception) {
                errorMensaje = "Error de conexión al cargar ejercicios."
            } finally {
                cargandoEjercicios = false
            }
        }
    }

    // ── Guardado ──────────────────────────────────────────────────────────────
    var guardando by mutableStateOf(false)
    var rutinaGuardadaExitosamente by mutableStateOf(false)
    var errorMensaje by mutableStateOf<String?>(null)

    fun guardarRutina() {
        if (nombreRutina.isBlank()) { errorMensaje = "La rutina necesita un nombre."; return }
        if (ejerciciosSeleccionados.isEmpty()) { errorMensaje = "Agrega al menos un ejercicio."; return }

        viewModelScope.launch {
            guardando = true
            errorMensaje = null
            try {
                // 1. Crear la rutina
                val rutinaResp = api.crearRutina(
                    CrearRutinaRequest(
                        nombreRutina = nombreRutina,
                        descripcion = descripcionRutina.ifBlank { null }
                    )
                )
                if (!rutinaResp.isSuccessful || rutinaResp.body() == null) {
                    errorMensaje = "No se pudo crear la rutina. Intenta de nuevo."
                    return@launch
                }

                val idRutinaCreada = rutinaResp.body()!!.idRutina

                // 2. Agregar cada ejercicio con sus parámetros
                ejerciciosSeleccionados.forEachIndexed { index, sel ->
                    api.agregarEjercicioARutina(
                        RutinaEjercicioRequest(
                            idRutina = idRutinaCreada,
                            idEjercicio = sel.idEjercicio,
                            orden = index + 1,
                            series = sel.series,
                            repeticiones = sel.repeticiones,
                            tiempoSeg = sel.tiempoSeg,
                            pesoReferencia = sel.pesoReferencia
                        )
                    )
                }

                rutinaGuardadaExitosamente = true

            } catch (e: Exception) {
                errorMensaje = "Error de conexión al guardar la rutina."
            } finally {
                guardando = false
            }
        }
    }

    fun limpiar() {
        nombreRutina = ""
        descripcionRutina = ""
        ejerciciosSeleccionados = emptyList()
        busqueda = ""
        zonaFiltro = null
        pasoActual = 1
        rutinaGuardadaExitosamente = false
        errorMensaje = null
    }
}
