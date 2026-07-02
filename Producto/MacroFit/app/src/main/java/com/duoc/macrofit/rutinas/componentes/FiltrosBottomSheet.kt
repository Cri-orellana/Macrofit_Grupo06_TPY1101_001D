package com.duoc.macrofit.rutinas.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosBottomSheet(
    niveles: List<String>,
    nivelSeleccionado: String?,
    onNivelClick: (String?) -> Unit,
    implementos: List<String>,
    implementoSeleccionado: String?,
    onImplementoClick: (String?) -> Unit,
    musculos: List<String>,
    musculoSeleccionado: String?,
    onMusculoClick: (String?) -> Unit,
    onLimpiarTodo: () -> Unit,
    onCerrar: () -> Unit
) {
    val verde = Color(0xFF76E320)

    ModalBottomSheet(
        onDismissRequest = onCerrar,
        containerColor = Color(0xFF161616)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Filtros", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                TextButton(onClick = onLimpiarTodo) {
                    Text("Limpiar todo", color = verde, fontSize = 13.sp)
                }
            }

            if (niveles.isNotEmpty()) {
                FiltroSeccion("Nivel de dificultad", niveles, nivelSeleccionado, onNivelClick, verde)
            }
            if (implementos.isNotEmpty()) {
                FiltroSeccion("Implemento", implementos, implementoSeleccionado, onImplementoClick, verde)
            }
            if (musculos.isNotEmpty()) {
                FiltroSeccion("Músculo objetivo", musculos, musculoSeleccionado, onMusculoClick, verde)
            }

            Button(
                onClick = onCerrar,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verde)
            ) {
                Text("Ver resultados", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// AQUÍ ESTÁ LA SOLUCIÓN: Agregamos la anotación para permitir FlowRow
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FiltroSeccion(
    titulo: String,
    opciones: List<String>,
    seleccionado: String?,
    onClick: (String?) -> Unit,
    verde: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { opcion ->
                val estaSeleccionado = seleccionado == opcion
                FilterChip(
                    selected = estaSeleccionado,
                    onClick = { onClick(if (estaSeleccionado) null else opcion) },
                    label = { Text(opcion) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = verde.copy(alpha = 0.2f),
                        selectedLabelColor = verde
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = estaSeleccionado,
                        selectedBorderColor = verde,
                        borderColor = Color(0xFF444444)
                    )
                )
            }
        }
    }
}