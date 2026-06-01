package com.duoc.macrofit.rutinas.view.componentes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * Barra de filtros para el selector de ejercicios.
 * Las zonas musculares vienen como strings distintos extraídos de la lista de ejercicios,
 * ya no se necesita el modelo Zona ni una llamada extra al backend.
 *
 * @param busqueda          Texto actual en el buscador.
 * @param onBusquedaChange  Callback al escribir en el buscador.
 * @param zonas             Lista de zonas musculares únicas (List<String>).
 * @param zonaSeleccionada  Zona actualmente seleccionada (null = todas).
 * @param onZonaClick       Callback al tocar un chip de zona.
 */
@Composable
fun FiltrosEjercicio(
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    zonas: List<String>,
    zonaSeleccionada: String?,
    onZonaClick: (String?) -> Unit
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

        // ── Chips de zona muscular (strings) ──────────────────────
        if (zonas.isNotEmpty()) {
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
            }
        }
    }
}
