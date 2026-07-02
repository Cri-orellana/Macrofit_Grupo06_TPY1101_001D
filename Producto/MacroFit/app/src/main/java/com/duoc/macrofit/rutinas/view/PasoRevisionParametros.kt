package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.view.componentes.EjercicioSeleccionadoCard
import com.duoc.macrofit.rutinas.viewmodel.CrearRutinaViewModel
import com.duoc.macrofit.rutinas.viewmodel.EjercicioSeleccionado
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*

@Composable
fun PasoRevisionParametros(viewModel: CrearRutinaViewModel) {
    val verde = Color(0xFF76E320)
    val colorFondo = Color(0xFF1A1A1A)

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
            colors = CardDefaults.cardColors(containerColor = colorFondo),
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

        Spacer(modifier = Modifier.height(16.dp))

        val ejerciciosPorDia = viewModel.ejerciciosSeleccionados
            .groupBy { it.dia }
            .toSortedMap()
            .mapValues { entry -> entry.value.sortedBy { it.orden ?: 0 } }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ejerciciosPorDia.forEach { (dia, ejerciciosDelDia) ->
                item(key = "titulo_dia_$dia") {
                    Column {
                        Text(
                            text = "Día $dia",
                            color = verde,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(color = verde.copy(alpha = 0.35f))
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                itemsIndexed(
                    items = ejerciciosDelDia,
                    key = { _, sel -> sel.idEjercicio }
                ) { index, sel ->
                    EjercicioSeleccionadoCard(
                        item = sel,
                        numero = index + 1,
                        cantidadDias = viewModel.cantidadDias,
                        onEliminar = { viewModel.eliminarEjercicio(sel.idEjercicio) },
                        onCambio = { actualizado -> viewModel.actualizarParametros(actualizado) },
                        onCambiarDia = { nuevoDia ->
                            viewModel.cambiarDiaEjercicio(sel.idEjercicio, nuevoDia)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(90.dp)) }
        }
    }
}