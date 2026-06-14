package com.duoc.macrofit.rutinas.view

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitFondoUniversal
import com.duoc.macrofit.rutinas.model.Rutina

@Composable
fun RutinasScreen(
    onCrearRutina: () -> Unit,
    onEditarRutina: (Rutina) -> Unit,
    refreshKey: Int = 0,
    viewModel: RutinasViewModel = viewModel()
) {
    val colorOscuro = Color(0xFF1A1A1A)

    LaunchedEffect(refreshKey) {
        viewModel.cargarRutinaActiva()

        if (viewModel.mostrarCatalogo) {
            viewModel.abrirCatalogo()
        }
    }

    MacroFitFondoUniversal {
        when {
            viewModel.mostrarCatalogo && viewModel.rutinaSeleccionadaCatalogo != null -> {
                DetalleRutinaCatalogoView(
                    viewModel = viewModel,
                    colorOscuro = colorOscuro,
                    onEditarRutina = onEditarRutina
                )
            }

            viewModel.mostrarCatalogo -> {
                CatalogoRutinasView(
                    viewModel = viewModel,
                    colorOscuro = colorOscuro
                )
            }

            else -> {
                RutinaActivaView(
                    viewModel = viewModel,
                    colorOscuro = colorOscuro,
                    onCrearRutina = onCrearRutina,
                    onEditarRutina = onEditarRutina
                )
            }
        }
    }
}