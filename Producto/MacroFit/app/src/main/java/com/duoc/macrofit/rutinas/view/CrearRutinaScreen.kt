package com.duoc.macrofit.rutinas.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.rutinas.view.componentes.EjercicioCard
import com.duoc.macrofit.rutinas.view.componentes.EjercicioSeleccionadoCard
import com.duoc.macrofit.rutinas.view.componentes.FiltrosEjercicio
import com.duoc.macrofit.rutinas.viewmodel.CrearRutinaViewModel
import com.duoc.macrofit.rutinas.viewmodel.EjercicioSeleccionado
import com.duoc.macrofit.usuarios.ui.screens.MacroFitFondoUniversal
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo

/**
 * Pantalla de creación de rutina personalizada en 3 pasos:
 * Paso 1 – Nombre y descripción de la rutina
 * Paso 2 – Selector de ejercicios con filtros
 * Paso 3 – Revisión de ejercicios seleccionados y ajuste de parámetros
 *
 * @param onVolver          Acción al presionar atrás en el paso 1.
 * @param onRutinaCreada    Callback cuando la rutina se guardó correctamente.
 */
@Composable
fun CrearRutinaScreen(
    onVolver: () -> Unit,
    onRutinaCreada: () -> Unit,
    idRutinaEditar: Int? = null,
    viewModel: CrearRutinaViewModel = viewModel()
) {
    LaunchedEffect(idRutinaEditar) {
        if (idRutinaEditar != null) {
            viewModel.cargarRutinaParaEditar(idRutinaEditar)
        }
    }

    if (viewModel.rutinaGuardadaExitosamente) {
        LaunchedEffect(Unit) {
            viewModel.limpiar()
            onRutinaCreada()
        }
    }

    val esModoEdicion = idRutinaEditar != null
    Log.d(
        "CREAR_SCREEN",
        "Pantalla abierta. idRutinaEditar=$idRutinaEditar, esModoEdicion=$esModoEdicion"
    )

    MacroFitFondoUniversal {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            MacroFitHeaderLogo()

            // ── Barra de progreso y navegación ────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (viewModel.pasoActual > 1) viewModel.retrocederPaso() else onVolver()
                }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                }
                LinearProgressIndicator(
                    progress = { viewModel.pasoActual / 3f },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color(0xFF333333)
                )
                Text(
                    "${viewModel.pasoActual}/3",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Contenido según el paso ───────────────────────────
            Box(modifier = Modifier.weight(1f)) {
                when (viewModel.pasoActual) {
                    1 -> PasoNombreDescripcion(viewModel)
                    2 -> PasoSelectorEjercicios(viewModel)
                    3 -> PasoRevisionParametros(viewModel)
                }
            }

            // ── Mensaje de error ──────────────────────────────────
            viewModel.errorMensaje?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // ── Botón de acción principal ─────────────────────────
            if (!viewModel.guardando) {
                Button(
                    onClick = {
                        Log.d(
                            "CREAR_SCREEN",
                            "Click botón principal. paso=${viewModel.pasoActual}, idRutinaEditar=$idRutinaEditar, esModoEdicion=$esModoEdicion"
                        )

                        if (viewModel.pasoActual < 3) {
                            viewModel.avanzarPaso()
                        } else {
                            if (esModoEdicion) {
                                viewModel.guardarEdicionRutina(idRutinaEditar!!)
                            } else {
                                viewModel.guardarRutina()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = when (viewModel.pasoActual) {
                            1 -> "Siguiente: Elegir Ejercicios"
                            2 -> "Siguiente: Ajustar Parámetros (${viewModel.ejerciciosSeleccionados.size})"
                            else -> if (esModoEdicion) "Guardar Cambios" else "Guardar Rutina"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

//Identidad de rutina
@Composable
private fun PasoNombreDescripcion(viewModel: CrearRutinaViewModel) {
    val verde = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nueva Rutina",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Dale un nombre y una descripción opcional.",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.nombreRutina,
            onValueChange = { viewModel.nombreRutina = it },
            label = { Text("Nombre de la rutina *") },
            placeholder = { Text("Ej: Fuerza PPL Lunes", color = Color.DarkGray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = camposColors(verde),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = viewModel.descripcionRutina,
            onValueChange = { viewModel.descripcionRutina = it },
            label = { Text("Descripción (opcional)") },
            placeholder = { Text("Ej: Empuje de pecho, hombro y tríceps", color = Color.DarkGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4,
            colors = camposColors(verde),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

//Selector ejercicios
@Composable
private fun PasoSelectorEjercicios(viewModel: CrearRutinaViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Elige Ejercicios",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Contador de seleccionados
        if (viewModel.ejerciciosSeleccionados.isNotEmpty()) {
            Text(
                text = "${viewModel.ejerciciosSeleccionados.size} seleccionado(s)",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filtros
        FiltrosEjercicio(
            busqueda = viewModel.busqueda,
            onBusquedaChange = { viewModel.busqueda = it },
            zonas = viewModel.zonasDisponibles,
            zonaSeleccionada = viewModel.zonaFiltro,
            onZonaClick = { viewModel.zonaFiltro = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            viewModel.cargandoEjercicios -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            viewModel.ejerciciosFiltrados.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (viewModel.busqueda.isNotBlank() || viewModel.zonaFiltro != null)
                            "Sin resultados con estos filtros."
                        else
                            "No hay ejercicios disponibles.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(viewModel.ejerciciosFiltrados) { _, ejercicio ->
                        EjercicioCard(
                            ejercicio = ejercicio,
                            seleccionado = viewModel.estaSeleccionado(ejercicio.idEjercicio),
                            onClick = { viewModel.toggleEjercicio(ejercicio) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

//Definicion de parametros
@Composable
private fun PasoRevisionParametros(viewModel: CrearRutinaViewModel) {
    val verde = MaterialTheme.colorScheme.primary

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Ajusta los Parámetros",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Define series, repeticiones, tiempo y peso para cada ejercicio.",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Resumen de la rutina
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = viewModel.nombreRutina,
                        fontWeight = FontWeight.Bold,
                        color = verde,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (viewModel.descripcionRutina.isNotBlank()) {
                        Text(
                            viewModel.descripcionRutina,
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = verde.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = verde, modifier = Modifier.size(14.dp))
                        Text(
                            "${viewModel.ejerciciosSeleccionados.size} ejercicios",
                            color = verde,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(
                viewModel.ejerciciosSeleccionados.sortedWith(
                    compareBy<EjercicioSeleccionado> { it.dia }
                        .thenBy { it.orden ?: 0 }
                )
            ) { index, sel ->
                EjercicioSeleccionadoCard(
                    item = sel,
                    numero = index + 1,
                    onEliminar = { viewModel.eliminarEjercicio(sel.idEjercicio) },
                    onCambio = { actualizado -> viewModel.actualizarParametros(actualizado) },
                    onCambiarDia = { nuevoDia ->
                        viewModel.cambiarDiaEjercicio(sel.idEjercicio, nuevoDia)
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helper de colores compartido para los campos de texto
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun camposColors(verde: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = verde,
    unfocusedBorderColor = Color(0xFF333333),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = verde,
    unfocusedLabelColor = Color.Gray,
    cursorColor = verde
)