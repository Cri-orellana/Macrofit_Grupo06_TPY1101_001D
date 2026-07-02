package com.duoc.macrofit.rutinas.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitFondoUniversal
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EstadisticasScreen(
    refreshKey: Int = 0,
    viewModel: RutinasViewModel = viewModel()
) {
    val verde = Color(0xFF76E320)
    val colorOscuro = Color(0xFF1A1A1A)

    LaunchedEffect(refreshKey) {
        viewModel.cargarHistorialRutinas()
    }

    MacroFitFondoUniversal {
        when {
            viewModel.cargandoHistorial -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = verde)
                }
            }
            viewModel.errorHistorial != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(viewModel.errorHistorial!!, color = Color.Red)
                }
            }
            else -> {
                HistorialRutinasView(
                    viewModel = viewModel,
                    colorOscuro = colorOscuro
                )
            }
        }
    }
}