package com.duoc.macrofit.macros.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.macros.viewmodel.ProgresoViewModel
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitFondoUniversal
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo

@Composable
fun ProgresoScreen(
    viewModel: ProgresoViewModel = viewModel(),
    rutinasViewModel: RutinasViewModel = viewModel(),
    onNavigateToEstadisticas: () -> Unit
) {
    val cargando by remember { derivedStateOf { viewModel.cargando } }
    val estadisticas = viewModel.listaFiltrada
    val periodoActual = viewModel.periodoSeleccionado
    val macroActual = viewModel.macroSeleccionado

    LaunchedEffect(Unit) {
        viewModel.cargarDatosHistoricos()
        rutinasViewModel.cargarRutinaActiva()
    }

    MacroFitFondoUniversal {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // <-- SOLUCIÓN: Agregamos el scroll a toda la pantalla
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MacroFitHeaderLogo()

            Text(
                text = "Resumen Estadístico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Selector de Periodo (Semana / Mes)
            TabRow(
                selectedTabIndex = listOf("Semana", "Mes").indexOf(periodoActual),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {}
            ) {
                listOf("Semana", "Mes").forEach { periodo ->
                    Tab(
                        selected = periodoActual == periodo,
                        onClick = { viewModel.cambiarPeriodo(periodo) },
                        text = { Text(periodo) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Macro
            ScrollableTabRow(
                selectedTabIndex = listOf("Calorías", "Proteínas", "Carbos", "Grasas").indexOf(macroActual),
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.secondary,
                edgePadding = 0.dp,
                divider = {}
            ) {
                listOf("Calorías", "Proteínas", "Carbos", "Grasas").forEach { macro ->
                    Tab(
                        selected = macroActual == macro,
                        onClick = { viewModel.cambiarMacro(macro) },
                        text = { Text(macro, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (cargando) {
                CircularProgressIndicator()
            } else if (estadisticas.isEmpty()) {
                Text("No hay datos para mostrar en este periodo", color = Color.Gray)
            } else {
                // Gráfico de Barras con Eje Y porcentual
                val limiteMacro = when (macroActual) {
                    "Calorías" -> viewModel.limiteCalorias
                    "Proteínas" -> viewModel.limiteProteinas
                    "Carbos" -> viewModel.limiteCarbos
                    "Grasas" -> viewModel.limiteGrasas
                    else -> 2000.0
                }

                // El eje Y mostrará hasta un 125% del límite (25% extra de margen)
                val maxEjeY = limiteMacro * 1.25

                Column {
                    Row(modifier = Modifier.fillMaxWidth().height(220.dp)) {
                        // Eje Y (Divisiones 0, 25, 50, 75, 100, 125%)
                        Column(
                            modifier = Modifier.fillMaxHeight().padding(end = 8.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.End
                        ) {
                            val unidad = if (macroActual == "Calorías") "" else "g"
                            listOf(1.25, 1.0, 0.75, 0.5, 0.25, 0.0).forEach { pct ->
                                Text(
                                    text = "${(limiteMacro * pct).toInt()}$unidad",
                                    fontSize = 9.sp,
                                    color = if (pct == 1.0) MaterialTheme.colorScheme.primary else Color.Gray,
                                    fontWeight = if (pct == 1.0) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        // Contenedor del gráfico con líneas de referencia
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            val primaryColor = MaterialTheme.colorScheme.primary
                            // Líneas de referencia (Grid)
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val gridColors = Color.Gray.copy(alpha = 0.2f)
                                // Dibujar líneas horizontales en 0, 20, 40, 60, 80, 100% de la altura visual
                                listOf(0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f).forEach { ratio ->
                                    val y = size.height * ratio
                                    drawLine(
                                        color = if (ratio == 0.2f) primaryColor.copy(alpha = 0.5f) else gridColors,
                                        start = Offset(0f, y),
                                        end = Offset(size.width, y),
                                        strokeWidth = if (ratio == 0.2f) 2f else 1f
                                    )
                                }
                            }

                            BarChart(
                                data = estadisticas,
                                macro = macroActual,
                                maxEjeY = maxEjeY,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Etiquetas de fecha
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 40.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val step = if (estadisticas.size > 15) 5 else 1
                        estadisticas.forEachIndexed { index, stat ->
                            if (index % step == 0) {
                                val label = stat.fecha.substringAfterLast("-")
                                Text(
                                    text = label,
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            } else if (estadisticas.size <= 15) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Navegación a Estadísticas de Rutinas
            OutlinedButton(
                onClick = onNavigateToEstadisticas,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Ver Estadísticas",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Ver Historial de Rutinas",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Progreso de Ejercicios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val ejercicios = rutinasViewModel.ejerciciosEnRutina
            if (ejercicios.isEmpty()) {
                Text("No hay rutina activa para mostrar progreso", color = Color.Gray)
            } else {
                // SOLUCIÓN: Cambiamos el LazyColumn por un Column normal con forEach
                // para evitar choques con el scroll de la pantalla completa
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ejercicios.forEach { ej ->
                        EjercicioProgresoItem(ej.detalle?.nombreEjercicio ?: "Ejercicio", ej.parametros)
                    }
                }
            }

            // Un pequeño espaciado final para que no quede pegado al menú inferior
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
fun BarChart(
    data: List<com.duoc.macrofit.macros.viewmodel.EstadisticaDiaria>,
    macro: String,
    maxEjeY: Double,
    modifier: Modifier
) {
    val colorBarra = when (macro) {
        "Calorías" -> MaterialTheme.colorScheme.primary
        "Proteínas" -> Color(0xFFE57373)
        "Carbos" -> Color(0xFF81C784)
        "Grasas" -> Color(0xFFFFB74D)
        else -> MaterialTheme.colorScheme.primary
    }

    Canvas(modifier = modifier) {
        val totalItems = data.size
        // Ancho de barra y espaciado dinámico
        val barWidth = if (totalItems > 10) size.width / (totalItems * 1.2f) else size.width / (totalItems * 1.8f)
        val space = (size.width - (barWidth * totalItems)) / (totalItems + 1)

        data.forEachIndexed { index, stat ->
            val value = when (macro) {
                "Calorías" -> stat.calorias
                "Proteínas" -> stat.proteinas
                "Carbos" -> stat.carbohidratos
                "Grasas" -> stat.grasas
                else -> 0.0
            }

            val barHeight = (value / maxEjeY) * size.height
            val x = space + index * (barWidth + space)
            val y = (size.height - barHeight.toFloat()).coerceAtLeast(0f)

            drawRect(
                color = colorBarra,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight.toFloat().coerceAtMost(size.height))
            )
        }
    }
}

@Composable
fun EjercicioProgresoItem(nombre: String, params: com.duoc.macrofit.rutinas.model.RutinaEjercicio) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "Series: ${params.series ?: "-"} | Reps: ${params.repeticiones ?: "-"}",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (params.pesoReferencia != null) "${params.pesoReferencia} kg" else "-",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (params.tiempoSeg != null) {
                    Text("${params.tiempoSeg} seg", fontSize = 11.sp, color = Color.Gray)
                }
            }
        }
    }
}