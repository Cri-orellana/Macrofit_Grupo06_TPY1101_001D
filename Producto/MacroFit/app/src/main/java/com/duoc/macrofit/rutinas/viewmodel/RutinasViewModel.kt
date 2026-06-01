package com.duoc.macrofit.rutinas.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.rutinas.api.RetrofitRutinas
import com.duoc.macrofit.rutinas.model.AsignarRutinaRequest
import com.duoc.macrofit.rutinas.model.Ejercicio
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.rutinas.model.RutinaEjercicio
import com.duoc.macrofit.rutinas.model.RutinaUsuario
import com.duoc.macrofit.usuarios.utils.SessionManager
import kotlinx.coroutines.launch
import java.time.LocalDate

// Modelo auxiliar para mostrar ejercicio + sus parámetros juntos en la UI
data class EjercicioEnRutina(
    val parametros: RutinaEjercicio,
    val detalle: Ejercicio?
)

class RutinasViewModel : ViewModel() {

    private val api = RetrofitRutinas.apiService

    // Estado de carga y errores
    var cargando by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    // Rutina activa del usuario
    var asignacionActiva by mutableStateOf<RutinaUsuario?>(null)
    var rutinaActiva by mutableStateOf<Rutina?>(null)
    var ejerciciosEnRutina by mutableStateOf<List<EjercicioEnRutina>>(emptyList())

    // Catálogo de rutinas disponibles
    var rutinasDisponibles by mutableStateOf<List<Rutina>>(emptyList())
    var rutinaSeleccionadaCatalogo by mutableStateOf<Rutina?>(null)
    var ejerciciosCatalogo by mutableStateOf<List<EjercicioEnRutina>>(emptyList())

    // Control de vista
    var mostrarCatalogo by mutableStateOf(false)
    var asignandoRutina by mutableStateOf(false)
    var asignacionExitosa by mutableStateOf(false)

    var creandoRutina by mutableStateOf(false)

    var nombreNuevaRutina by mutableStateOf("")
    var descripcionNuevaRutina by mutableStateOf("")

    var ejerciciosDisponibles by mutableStateOf<List<Ejercicio>>(emptyList())
    var ejerciciosSeleccionados by mutableStateOf<List<Ejercicio>>(emptyList())

    var filtroZonaMuscular by mutableStateOf<String?>(null)
    var filtroImplemento by mutableStateOf<String?>(null)
    var filtroNivelDificultad by mutableStateOf<String?>(null)
    var filtroMusculoObjetivo by mutableStateOf<String?>(null)

    init {
        cargarRutinaActiva()
    }

    fun cargarRutinaActiva() {
        val idUsuario = SessionManager.usuarioActual?.id ?: return
        viewModelScope.launch {
            cargando = true
            error = null
            asignacionExitosa = false
            try {
                val respuesta = api.obtenerRutinaActiva(idUsuario)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    asignacionActiva = respuesta.body()
                    val idRutina = respuesta.body()!!.idRutina
                    cargarDetalleRutina(idRutina)
                    cargarEjerciciosDeRutina(idRutina) { lista ->
                        ejerciciosEnRutina = lista
                    }
                } else {
                    // HTTP 404: el usuario no tiene rutina activa
                    asignacionActiva = null
                    rutinaActiva = null
                    ejerciciosEnRutina = emptyList()
                }
            } catch (e: Exception) {
                error = "No se pudo conectar con el servidor de rutinas."
            } finally {
                cargando = false
            }
        }
    }

    private suspend fun cargarDetalleRutina(idRutina: Int) {
        val resp = api.obtenerRutinaPorId(idRutina)
        if (resp.isSuccessful) rutinaActiva = resp.body()
    }

    private suspend fun cargarEjerciciosDeRutina(
        idRutina: Int,
        onResult: (List<EjercicioEnRutina>) -> Unit
    ) {
        val resp = api.obtenerEjerciciosDeRutina(idRutina)
        if (resp.isSuccessful && resp.body() != null) {
            val lista = resp.body()!!.map { re ->
                val ejercicioResp = api.obtenerEjercicioPorId(re.idEjercicio)
                EjercicioEnRutina(
                    parametros = re,
                    detalle = if (ejercicioResp.isSuccessful) ejercicioResp.body() else null
                )
            }
            onResult(lista)
        }
    }

    fun abrirCatalogo() {
        mostrarCatalogo = true
        rutinaSeleccionadaCatalogo = null
        ejerciciosCatalogo = emptyList()
        viewModelScope.launch {
            try {
                val resp = api.obtenerRutinasActivas()
                if (resp.isSuccessful) rutinasDisponibles = resp.body() ?: emptyList()
            } catch (e: Exception) {
                error = "Error al cargar el catálogo de rutinas."
            }
        }
    }

    fun cargarEjerciciosFiltrados() {
        viewModelScope.launch {
            cargando = true
            error = null
            try {
                val resp = api.filtrarEjercicios(
                    zonaMuscular = filtroZonaMuscular,
                    implemento = filtroImplemento,
                    nivelDificultad = filtroNivelDificultad,
                    musculoObjetivo = filtroMusculoObjetivo
                )

                if (resp.isSuccessful) {
                    ejerciciosDisponibles = resp.body() ?: emptyList()
                } else {
                    error = "No se pudieron cargar los ejercicios."
                }
            } catch (e: Exception) {
                error = "Error de conexión al cargar ejercicios."
            } finally {
                cargando = false
            }
        }
    }

    fun verDetallesRutinaCatalogo(rutina: Rutina) {
        rutinaSeleccionadaCatalogo = rutina
        viewModelScope.launch {
            cargando = true
            try {
                cargarEjerciciosDeRutina(rutina.idRutina) { lista ->
                    ejerciciosCatalogo = lista
                }
            } catch (e: Exception) {
                error = "Error al cargar los ejercicios."
            } finally {
                cargando = false
            }
        }
    }

    fun alternarSeleccionEjercicio(ejercicio: Ejercicio) {
        ejerciciosSeleccionados =
            if (ejerciciosSeleccionados.any { it.idEjercicio == ejercicio.idEjercicio }) {
                ejerciciosSeleccionados.filter { it.idEjercicio != ejercicio.idEjercicio }
            } else {
                ejerciciosSeleccionados + ejercicio
            }
    }

    fun asignarRutina(idRutina: Int) {
        val idUsuario = SessionManager.usuarioActual?.id ?: return
        viewModelScope.launch {
            asignandoRutina = true
            error = null
            try {
                // Desactivar asignación previa si existe
                asignacionActiva?.let {
                    api.desactivarAsignacion(it.idRutinaUsuario)
                }

                val request = AsignarRutinaRequest(
                    idRutina = idRutina,
                    idUsuario = idUsuario,
                    fechaInicio = LocalDate.now().toString()
                )
                val resp = api.asignarRutina(request)
                if (resp.isSuccessful) {
                    asignacionExitosa = true
                    mostrarCatalogo = false
                    rutinaSeleccionadaCatalogo = null
                    cargarRutinaActiva()
                } else {
                    error = "No se pudo asignar la rutina."
                }
            } catch (e: Exception) {
                error = "Error de conexión al asignar la rutina."
            } finally {
                asignandoRutina = false
            }
        }
    }

    fun cerrarCatalogo() {
        mostrarCatalogo = false
        rutinaSeleccionadaCatalogo = null
        ejerciciosCatalogo = emptyList()
    }

    fun limpiarError() {
        error = null
    }
}