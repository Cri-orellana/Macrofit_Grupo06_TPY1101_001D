package com.duoc.macrofit.rutinas.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.rutinas.viewmodel.CrearRutinaViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitFondoUniversal
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo

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

                // Forzamos el tipo Float explícitamente para evitar la ambigüedad en Compose
                val progresoCalculado: Float = viewModel.pasoActual / 3f

                LinearProgressIndicator(
                    progress = progresoCalculado,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Color(0xFF333333)
                )
                Text(
                    text = "${viewModel.pasoActual}/3",
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