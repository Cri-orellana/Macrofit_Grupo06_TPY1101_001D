package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.duoc.macrofit.rutinas.componentes.FiltrosBottomSheet
import com.duoc.macrofit.rutinas.view.componentes.EjercicioCard
import com.duoc.macrofit.rutinas.view.componentes.FiltrosEjercicio
import com.duoc.macrofit.rutinas.viewmodel.CrearRutinaViewModel
@Composable
fun PasoSelectorEjercicios(viewModel: CrearRutinaViewModel) {
    val verde = Color(0xFF76E320)
    var mostrarFiltros by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Elige Ejercicios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (viewModel.ejerciciosSeleccionados.isNotEmpty()) {
                Text(
                    text = "${viewModel.ejerciciosSeleccionados.size} seleccionado(s)",
                    color = verde,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FiltrosEjercicio(
            busqueda = viewModel.busqueda,
            onBusquedaChange = { viewModel.busqueda = it },
            zonas = viewModel.zonasDisponibles,
            zonaSeleccionada = viewModel.zonaFiltro,
            onZonaClick = { viewModel.zonaFiltro = it },
            cantidadFiltrosExtra = viewModel.cantidadFiltrosExtra,
            onFiltrosClick = { mostrarFiltros = true }
        )
        if (mostrarFiltros) {
            FiltrosBottomSheet(
                niveles = viewModel.nivelesDisponibles,
                nivelSeleccionado = viewModel.nivelFiltro,
                onNivelClick = { viewModel.nivelFiltro = it },
                implementos = viewModel.implementosDisponibles,
                implementoSeleccionado = viewModel.implementoFiltro,
                onImplementoClick = { viewModel.implementoFiltro = it },
                musculos = viewModel.musculosObjetivoDisponibles,
                musculoSeleccionado = viewModel.musculoObjetivoFiltro,
                onMusculoClick = { viewModel.musculoObjetivoFiltro = it },
                onLimpiarTodo = { viewModel.limpiarFiltrosExtra() },
                onCerrar = { mostrarFiltros = false }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        when {
            viewModel.cargandoEjercicios -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = verde)
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
                Text(
                    text = "${viewModel.ejerciciosFiltrados.size} ejercicios encontrados",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    itemsIndexed(viewModel.ejerciciosFiltrados) { _, ejercicio ->
                        EjercicioCard(
                            ejercicio = ejercicio,
                            seleccionado = viewModel.estaSeleccionado(ejercicio.idEjercicio),
                            onClick = { viewModel.toggleEjercicio(ejercicio) }
                        )
                    }
                }
            }
        }
    }
}