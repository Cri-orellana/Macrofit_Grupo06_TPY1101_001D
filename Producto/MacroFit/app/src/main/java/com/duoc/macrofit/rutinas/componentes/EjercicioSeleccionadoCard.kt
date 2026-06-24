package com.duoc.macrofit.rutinas.view.componentes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.viewmodel.EjercicioSeleccionado

/**
 * Tarjeta para un ejercicio ya agregado a la rutina que se está creando.
 * Permite editar series, repeticiones, tiempo y peso directamente.
 *
 * @param item     El ejercicio seleccionado con sus parámetros actuales.
 * @param numero   Posición en la lista (orden visual).
 * @param onEliminar Callback para quitarlo de la lista.
 * @param onCambio Callback cuando el usuario edita algún parámetro.
 * @param onCambiarDia Callback para asignar el ejercicio a un día específico.
 */
@Composable
fun EjercicioSeleccionadoCard(
    item: EjercicioSeleccionado,
    numero: Int,
    onEliminar: () -> Unit,
    onCambio: (EjercicioSeleccionado) -> Unit,
    onCambiarDia: (Int) -> Unit
) {
    val verde = MaterialTheme.colorScheme.primary
    val colorFondo = Color(0xFF1E1E1E)

    // Estados locales para los campos de texto
    var series by remember(item.idEjercicio) { mutableStateOf(item.series?.toString() ?: "") }
    var reps by remember(item.idEjercicio) { mutableStateOf(item.repeticiones?.toString() ?: "") }
    var tiempo by remember(item.idEjercicio) { mutableStateOf(item.tiempoSeg?.toString() ?: "") }
    var peso by remember(item.idEjercicio) { mutableStateOf(item.pesoReferencia?.toString() ?: "") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Encabezado: número, nombre y botón eliminar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Número de orden
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = verde.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "$numero",
                            fontWeight = FontWeight.Bold,
                            color = verde,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Ícono de drag (visual, sin funcionalidad de arrastre por ahora)
                Icon(
                    Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = item.nombreEjercicio,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onEliminar,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nueva sección agregada: Selección de Día Asignado
            Text(
                text = "Día asignado",
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..7).forEach { dia ->
                    val seleccionado = item.dia == dia

                    FilterChip(
                        selected = seleccionado,
                        onClick = { onCambiarDia(dia) },
                        label = { Text("Día $dia") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = verde.copy(alpha = 0.2f),
                            selectedLabelColor = verde,
                            labelColor = Color.LightGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionado,
                            selectedBorderColor = verde,
                            borderColor = Color(0xFF444444)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2A2A2A))
            Spacer(modifier = Modifier.height(12.dp))

            // Campos de parámetros en grid 2x2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoParametro(
                    label = "Series",
                    valor = series,
                    modifier = Modifier.weight(1f),
                    onValorChange = {
                        series = it
                        onCambio(item.copy(series = it.toIntOrNull()))
                    }
                )
                CampoParametro(
                    label = "Reps",
                    valor = reps,
                    modifier = Modifier.weight(1f),
                    onValorChange = {
                        reps = it
                        onCambio(item.copy(repeticiones = it.toIntOrNull()))
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CampoParametro(
                    label = "Tiempo (seg)",
                    valor = tiempo,
                    modifier = Modifier.weight(1f),
                    onValorChange = {
                        tiempo = it
                        onCambio(item.copy(tiempoSeg = it.toIntOrNull()))
                    }
                )
                CampoParametro(
                    label = "Peso (kg)",
                    valor = peso,
                    modifier = Modifier.weight(1f),
                    esDecimal = true,
                    onValorChange = {
                        peso = it
                        onCambio(item.copy(pesoReferencia = it.toFloatOrNull()))
                    }
                )
            }
        }
    }
}

@Composable
private fun CampoParametro(
    label: String,
    valor: String,
    modifier: Modifier = Modifier,
    esDecimal: Boolean = false,
    onValorChange: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (esDecimal) KeyboardType.Decimal else KeyboardType.Number
        ),
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color(0xFF333333),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = Color.Gray,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(10.dp)
    )
}