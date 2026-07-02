package com.duoc.macrofit.rutinas.view.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Barra de filtros para el selector de ejercicios.
 * Las zonas musculares vienen como strings distintos extraídos de la lista de ejercicios,
 * ya no se necesita el modelo Zona ni una llamada extra al backend.
 *
 * @param busqueda             Texto actual en el buscador.
 * @param onBusquedaChange     Callback al escribir en el buscador.
 * @param zonas                Lista de zonas musculares únicas (List<String>).
 * @param zonaSeleccionada     Zona actualmente seleccionada (null = todas).
 * @param onZonaClick          Callback al tocar un chip de zona.
 * @param cantidadFiltrosExtra Cantidad de filtros adicionales aplicados.
 * @param onFiltrosClick       Callback al tocar el botón de filtros extra.
 */
@Composable
fun FiltrosEjercicio(
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    zonas: List<String>,
    zonaSeleccionada: String?,
    onZonaClick: (String?) -> Unit,
    cantidadFiltrosExtra: Int,
    onFiltrosClick: () -> Unit
) {
    val verde = MaterialTheme.colorScheme.primary
    val focusManager = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // ── Buscador ──────────────────────────────────────────────
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar ejercicio...", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
            },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { onBusquedaChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Limpiar", tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = verde,
                unfocusedBorderColor = Color(0xFF333333),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = verde
            ),
            shape = RoundedCornerShape(12.dp)
        )

        // ── Chips de zona muscular (strings) y Filtros Extras ──────────────────────
        if (zonas.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text("Filtrar por zona", color = Color.Gray, fontSize = 12.sp)
                }

                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Chip "Todas"
                        FilterChip(
                            selected = zonaSeleccionada == null,
                            onClick = { onZonaClick(null) },
                            label = { Text("Todas") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = verde.copy(alpha = 0.2f),
                                selectedLabelColor = verde
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = zonaSeleccionada == null,
                                selectedBorderColor = verde,
                                borderColor = Color(0xFF444444)
                            )
                        )

                        zonas.forEach { zona ->
                            val seleccionado = zonaSeleccionada == zona
                            FilterChip(
                                selected = seleccionado,
                                onClick = { onZonaClick(if (seleccionado) null else zona) },
                                label = { Text(zona) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = verde.copy(alpha = 0.2f),
                                    selectedLabelColor = verde
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = seleccionado,
                                    selectedBorderColor = verde,
                                    borderColor = Color(0xFF444444)
                                )
                            )
                        }

                        // Chip de Filtros Extras
                        AssistChip(
                            onClick = onFiltrosClick,
                            label = {
                                Text(if (cantidadFiltrosExtra > 0) "Filtros ($cantidadFiltrosExtra)" else "Filtros")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (cantidadFiltrosExtra > 0) verde.copy(alpha = 0.2f) else Color.Transparent,
                                labelColor = if (cantidadFiltrosExtra > 0) verde else Color.LightGray,
                                leadingIconContentColor = if (cantidadFiltrosExtra > 0) verde else Color.LightGray
                            ),
                            border = AssistChipDefaults.assistChipBorder(
                                enabled = true,
                                borderColor = if (cantidadFiltrosExtra > 0) verde else Color(0xFF444444)
                            )
                        )

                        // Padding final para que la última chip no quede pegada al fade
                        Spacer(Modifier.width(4.dp))
                    }

                    // Fade indicando que hay más contenido a la derecha
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(24.dp)
                            .height(36.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                )
                            )
                    )
                }
            }
        }
    }
}