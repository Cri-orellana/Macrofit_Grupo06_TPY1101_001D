package com.duoc.macrofit.rutinas.view.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.model.Ejercicio

/**
 * Tarjeta de ejercicio para el selector/catálogo.
 * Muestra nombre, zona muscular (ya viene como String), dificultad y un botón toggle.
 *
 * @param ejercicio    El ejercicio a mostrar.
 * @param seleccionado Si ya fue agregado a la rutina que se está creando.
 * @param onClick      Acción al tocar la tarjeta (toggle selección).
 */
@Composable
fun EjercicioCard(
    ejercicio: Ejercicio,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val verde = MaterialTheme.colorScheme.primary
    val colorFondo = Color(0xFF1A1A1A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) verde.copy(alpha = 0.12f) else colorFondo
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (seleccionado) BorderStroke(1.5.dp, verde) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Iniciales del ejercicio como avatar
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(10.dp),
                color = verde.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = ejercicio.nombreEjercicio.take(2).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = verde
                    )
                }
            }

            // Nombre, zona y dificultad
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ejercicio.nombreEjercicio,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Zona muscular (viene directamente como String)
                ejercicio.zonaMuscular?.let { zona ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = zona,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Fila de chips: músculo objetivo + dificultad + implemento
                val chips = listOfNotNull(
                    ejercicio.musculoObjetivo,
                    ejercicio.nivelDificultad,
                    ejercicio.implemento
                )
                if (chips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        chips.take(2).forEach { chip ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFF2A2A2A)
                            ) {
                                Text(
                                    text = chip,
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Botón agregar/quitar
            FilledIconButton(
                onClick = onClick,
                modifier = Modifier.size(36.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (seleccionado) verde else verde.copy(alpha = 0.2f),
                    contentColor = if (seleccionado) Color.Black else verde
                )
            ) {
                Icon(
                    imageVector = if (seleccionado) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = if (seleccionado) "Quitar" else "Agregar",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

