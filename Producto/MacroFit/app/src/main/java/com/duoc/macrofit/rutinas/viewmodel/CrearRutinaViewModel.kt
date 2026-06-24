package com.duoc.macrofit.rutinas.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.macrofit.rutinas.api.RetrofitRutinas
import com.duoc.macrofit.rutinas.model.CrearRutinaRequest
import com.duoc.macrofit.rutinas.model.Ejercicio
import com.duoc.macrofit.rutinas.model.RutinaEjercicio
import com.duoc.macrofit.rutinas.model.RutinaEjercicioRequest
import com.duoc.macrofit.usuarios.utils.SessionManager
import kotlinx.coroutines.launch

/**
 * Representa un ejercicio seleccionado para la rutina que se está creando,
 * junto con sus parámetros editables y el día asignado.
 */
data class EjercicioSeleccionado(
    val idEjercicio: Int,
    val nombreEjercicio: String,
    val dia: Int = 1,
    val orden: Int? = null,
    val series: Int? = 3,
    val repeticiones: Int? = 10,
    val tiempoSeg: Int? = null,
    val pesoReferencia: Float? = null
)

class CrearRutinaViewModel : ViewModel() {

    private val api = RetrofitRutinas.apiService

    var cargandoRutinaEditar by mutableStateOf(false)
    var yaCargoRutinaEditar by mutableStateOf(false)

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

    fun cambiarDiaEjercicio(idEjercicio: Int, nuevoDia: Int) {
        ejerciciosSeleccionados = ejerciciosSeleccionados.map { ejercicio ->
            if (ejercicio.idEjercicio == idEjercicio) {
                ejercicio.copy(dia = nuevoDia)
            } else {
                ejercicio
            }
        }
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
        Log.d("CREAR_VM", "Ejecutando guardarRutina() -> CREAR NUEVA")

        if (nombreRutina.isBlank()) { errorMensaje = "La rutina necesita un nombre."; return }
        if (ejerciciosSeleccionados.isEmpty()) { errorMensaje = "Agrega al menos un ejercicio."; return }

        // Verificar que hay sesión activa antes de continuar
        val idUsuario = SessionManager.usuarioActual?.id ?: run {
            errorMensaje = "No hay sesión activa."
            return
        }

        viewModelScope.launch {
            guardando = true
            errorMensaje = null
            try {
                //Crear la rutina
                val rutinaResp = api.crearRutina(
                    idUsuario = idUsuario,
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

                //Agregar cada ejercicio con sus parametros, respetando el día
                ejerciciosSeleccionados.forEachIndexed { index, sel ->
                    val ejercicioResp = api.agregarEjercicioARutina(
                        RutinaEjercicioRequest(
                            idRutina = idRutinaCreada,
                            idEjercicio = sel.idEjercicio,
                            dia = sel.dia,
                            orden = index + 1,
                            series = sel.series,
                            repeticiones = sel.repeticiones,
                            tiempoSeg = sel.tiempoSeg,
                            pesoReferencia = sel.pesoReferencia
                        )
                    )
                    if (!ejercicioResp.isSuccessful) {
                        errorMensaje = "La rutina se creó, pero falló al agregar un ejercicio."
                        return@launch
                    }
                }

                rutinaGuardadaExitosamente = true

            } catch (e: Exception) {
                errorMensaje = "Error de conexión al guardar la rutina."
            } finally {
                guardando = false
            }
        }
    }

    fun cargarRutinaParaEditar(idRutina: Int) {
        if (yaCargoRutinaEditar) return

        viewModelScope.launch {
            cargandoRutinaEditar = true
            errorMensaje = null

            try {
                val respRutina = api.obtenerRutinaPorId(idRutina)

                if (!respRutina.isSuccessful || respRutina.body() == null) {
                    errorMensaje = "No se pudo cargar la rutina para editar."
                    return@launch
                }

                val rutina = respRutina.body()!!

                nombreRutina = rutina.nombreRutina
                descripcionRutina = rutina.descripcion ?: ""

                if (todosLosEjercicios.isEmpty()) {
                    val respEjercicios = api.obtenerEjerciciosActivos()
                    if (respEjercicios.isSuccessful) {
                        todosLosEjercicios = respEjercicios.body() ?: emptyList()
                    }
                }
                val respRutinaEjercicios = api.obtenerEjerciciosDeRutina(idRutina)

                if (!respRutinaEjercicios.isSuccessful) {
                    errorMensaje = "No se pudieron cargar los ejercicios de la rutina."
                    return@launch
                }

                val ejerciciosRutina = respRutinaEjercicios.body() ?: emptyList()

                // Se ordena por día y luego por orden
                ejerciciosSeleccionados = ejerciciosRutina
                    .sortedWith(
                        compareBy<RutinaEjercicio> { it.dia ?: 1 }
                            .thenBy { it.orden ?: 0 }
                    )
                    .map { re ->
                        val ejercicio = todosLosEjercicios.find { it.idEjercicio == re.idEjercicio }

                        EjercicioSeleccionado(
                            idEjercicio = re.idEjercicio,
                            nombreEjercicio = ejercicio?.nombreEjercicio ?: "Ejercicio #${re.idEjercicio}",
                            dia = re.dia ?: 1,
                            orden = re.orden,
                            series = re.series,
                            repeticiones = re.repeticiones,
                            tiempoSeg = re.tiempoSeg,
                            pesoReferencia = re.pesoReferencia
                        )
                    }
                yaCargoRutinaEditar = true
            } catch (e: Exception) {
                errorMensaje = "Error al cargar rutina para editar: ${e.message}"
            } finally {
                cargandoRutinaEditar = false
            }
        }
    }

    fun guardarEdicionRutina(idRutina: Int) {
        Log.d("CREAR_VM", "Ejecutando guardarEdicionRutina($idRutina) -> EDITAR EXISTENTE")
        if (nombreRutina.isBlank()) {
            errorMensaje = "La rutina necesita un nombre."
            return
        }
        if (ejerciciosSeleccionados.isEmpty()) {
            errorMensaje = "Agrega al menos un ejercicio."
            return
        }
        val idUsuario = SessionManager.usuarioActual?.id ?: run {
            errorMensaje = "No hay sesión activa."
            return
        }
        viewModelScope.launch {
            guardando = true
            errorMensaje = null

            try {
                val respRutina = api.editarRutina(
                    idRutina = idRutina,
                    idUsuario = idUsuario,
                    request = CrearRutinaRequest(
                        nombreRutina = nombreRutina,
                        descripcion = descripcionRutina.ifBlank { null }
                    )
                )
                if (!respRutina.isSuccessful) {
                    errorMensaje = "No se pudo editar la rutina. Código: ${respRutina.code()}"
                    return@launch
                }

                // Se agrupan los ejercicios por día para mantener el orden correcto por jornada
                val ejerciciosRequest = ejerciciosSeleccionados
                    .groupBy { it.dia }
                    .flatMap { (dia, ejerciciosDelDia) ->
                        ejerciciosDelDia.mapIndexed { index, sel ->
                            RutinaEjercicioRequest(
                                idRutina = idRutina,
                                idEjercicio = sel.idEjercicio,
                                dia = dia,
                                orden = index + 1,
                                series = sel.series,
                                repeticiones = sel.repeticiones,
                                tiempoSeg = sel.tiempoSeg,
                                pesoReferencia = sel.pesoReferencia
                            )
                        }
                    }

                val respEjercicios = api.reemplazarEjerciciosDeRutina(
                    idRutina = idRutina,
                    ejercicios = ejerciciosRequest
                )
                if (!respEjercicios.isSuccessful) {
                    errorMensaje = "La rutina se editó, pero falló al actualizar ejercicios. Código: ${respEjercicios.code()}"
                    return@launch
                }
                rutinaGuardadaExitosamente = true
            } catch (e: Exception) {
                errorMensaje = "Error al guardar edición: ${e.message}"
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
        cargandoRutinaEditar = false
        yaCargoRutinaEditar = false
    }
}