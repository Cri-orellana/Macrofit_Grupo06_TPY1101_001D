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
import com.duoc.macrofit.rutinas.model.RutinaUsuarioHistorialDTO // --- INTEGRADO DEL CÓDIGO 1 ---
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import com.duoc.macrofit.usuarios.utils.SessionManager

// Modelo auxiliar para mostrar ejercicio + sus parámetros juntos en la UI
data class EjercicioEnRutina(
    val parametros: RutinaEjercicio,
    val detalle: Ejercicio?
)

class RutinasViewModel : ViewModel() {

    private val api = RetrofitRutinas.apiService

    var preparandoEdicion by mutableStateOf(false)
    var rutinaParaEditar by mutableStateOf<Rutina?>(null)

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

    // --- INTEGRADO DEL CÓDIGO 1: Historial ---
    var historialRutinas by mutableStateOf<List<RutinaUsuarioHistorialDTO>>(emptyList())
    var cargandoHistorial by mutableStateOf(false)
    var errorHistorial by mutableStateOf<String?>(null)

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
        val idUsuario = SessionManager.usuarioActual?.id ?: run {
            error = "No hay sesión activa."
            return
        }

        mostrarCatalogo = true
        rutinaSeleccionadaCatalogo = null
        ejerciciosCatalogo = emptyList()
        rutinasDisponibles = emptyList()

        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val respCatalogo = api.obtenerRutinasActivas()
                val respUsuario = api.obtenerRutinasPorUsuario(idUsuario)

                if (!respCatalogo.isSuccessful) {
                    error = "Error al cargar rutinas del catálogo. Código: ${respCatalogo.code()}"
                    return@launch
                }

                if (!respUsuario.isSuccessful) {
                    error = "Error al cargar rutinas del usuario. Código: ${respUsuario.code()}"
                    return@launch
                }

                val rutinasCatalogo = respCatalogo.body() ?: emptyList()
                val rutinasUsuario = respUsuario.body() ?: emptyList()

                rutinasDisponibles = (rutinasCatalogo + rutinasUsuario)
                    .distinctBy { it.idRutina }
                    .sortedByDescending { it.idUsuarioCreador == idUsuario }

            } catch (e: Exception) {
                error = "Error al cargar rutinas: ${e.message}"
            } finally {
                cargando = false
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
        val idUsuario = SessionManager.usuarioActual?.id ?: run {
            error = "No hay sesión activa."
            return
        }

        viewModelScope.launch {
            asignandoRutina = true
            cargando = true
            error = null

            try {
                // Desactivar asignación previa si existe
                asignacionActiva?.let { asignacion ->
                    val respDesactivar = api.desactivarAsignacion(asignacion.idRutinaUsuario)

                    if (!respDesactivar.isSuccessful) {
                        error = "No se pudo desactivar la rutina anterior. Código: ${respDesactivar.code()}"
                        return@launch
                    }
                }

                val fechaActual = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date())

                val request = AsignarRutinaRequest(
                    idRutina = idRutina,
                    idUsuario = idUsuario,
                    fechaInicio = fechaActual
                )

                val resp = api.asignarRutina(request)

                if (!resp.isSuccessful || resp.body() == null) {
                    error = "No se pudo asignar la rutina. Código: ${resp.code()}"
                    return@launch
                }

                val nuevaAsignacion = resp.body()!!

                asignacionActiva = nuevaAsignacion

                cargarDetalleRutina(nuevaAsignacion.idRutina)

                cargarEjerciciosDeRutina(nuevaAsignacion.idRutina) { lista ->
                    ejerciciosEnRutina = lista
                }

                asignacionExitosa = true
                mostrarCatalogo = false
                rutinaSeleccionadaCatalogo = null
                ejerciciosCatalogo = emptyList()

            } catch (e: Exception) {
                error = "Error de conexión al asignar la rutina: ${e.message}"
            } finally {
                asignandoRutina = false
                cargando = false
            }
        }
    }

    fun prepararEdicionRutina(
        rutina: Rutina,
        onEditarLista: (Rutina) -> Unit
    ) {
        val idUsuario = SessionManager.usuarioActual?.id ?: run {
            error = "No hay sesión activa."
            return
        }

        viewModelScope.launch {
            preparandoEdicion = true
            error = null

            try {
                val esRutinaPropia = rutina.idUsuarioCreador == idUsuario

                Log.d(
                    "EditarRutina",
                    "idRutina=${rutina.idRutina}, idUsuario=$idUsuario, idUsuarioCreador=${rutina.idUsuarioCreador}, activoCatalogo=${rutina.activoCatalogo}, esBase=${rutina.esBase}"
                )

                if (esRutinaPropia) {
                    rutinaParaEditar = rutina
                    onEditarLista(rutina)
                    return@launch
                }

                val resp = api.copiarRutinaBaseAUsuario(
                    idRutinaBase = rutina.idRutina,
                    idUsuario = idUsuario
                )

                if (resp.isSuccessful && resp.body() != null) {
                    val copia = resp.body()!!
                    rutinaParaEditar = copia
                    onEditarLista(copia)
                } else {
                    error = "No se pudo preparar la rutina para edición. Código: ${resp.code()}"
                }

            } catch (e: Exception) {
                error = "Error al preparar edición: ${e.message}"
            } finally {
                preparandoEdicion = false
            }
        }
    }

    fun eliminarRutinaPersonal(idRutina: Int) {
        val idUsuario = SessionManager.usuarioActual?.id ?: run {
            error = "No hay sesión activa."
            return
        }

        viewModelScope.launch {
            cargando = true
            error = null

            try {
                val resp = api.eliminarRutinaPersonal(
                    idRutina = idRutina,
                    idUsuario = idUsuario)
                if (resp.isSuccessful) {
                    if (rutinaActiva?.idRutina == idRutina) {
                        asignacionActiva = null
                        rutinaActiva = null
                        ejerciciosEnRutina = emptyList()}

                    rutinaSeleccionadaCatalogo = null
                    ejerciciosCatalogo = emptyList()
                    abrirCatalogo()
                } else {
                    error = "No se pudo eliminar la rutina. Código: ${resp.code()}"
                }

            } catch (e: Exception) {
                error = "Error al eliminar rutina: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    // --- INTEGRADO DEL CÓDIGO 1: Cargar Historial ---
    fun cargarHistorialRutinas() {
        val idUsuario = SessionManager.usuarioActual?.id ?: return
        viewModelScope.launch {
            cargandoHistorial = true
            errorHistorial = null
            try {
                val respuesta = api.obtenerHistorial(idUsuario)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    historialRutinas = respuesta.body()!!
                } else {
                    historialRutinas = emptyList()
                }
            } catch (e: Exception) {
                errorHistorial = "No se pudo conectar con el servidor de rutinas."
            } finally {
                cargandoHistorial = false
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Función integrada desde el primer bloque de código
    // ─────────────────────────────────────────────────────────────────────────────
    fun obtenerEjerciciosPorDia(): Map<Int, List<EjercicioEnRutina>> {
        return ejerciciosEnRutina
            .groupBy { it.parametros.dia ?: 1 }
            .toSortedMap()
            .mapValues { entry ->
                entry.value.sortedBy { it.parametros.orden ?: 0 }
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