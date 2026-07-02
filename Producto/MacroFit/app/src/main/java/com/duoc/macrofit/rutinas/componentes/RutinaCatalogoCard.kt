package com.duoc.macrofit.rutinas.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.usuarios.utils.SessionManager

@Composable
fun RutinaCatalogoCard(
    rutina: Rutina,
    colorOscuro: Color,
    onClick: () -> Unit
) {
    val idUsuarioActual = SessionManager.usuarioActual?.id
    val verde = MaterialTheme.colorScheme.primary

    val esRutinaPropia = rutina.idUsuarioCreador == idUsuarioActual
    val esRutinaCatalogo = rutina.activoCatalogo == true

    // Fusión: Textos del código 2 con los colores dinámicos del código 1
    val (textoTipo, colorTipo) = when {
        esRutinaPropia -> "Rutina personal" to verde
        esRutinaCatalogo -> "Rutina del catálogo" to Color(0xFF64B5F6)
        else -> "Rutina" to Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = colorOscuro),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Ícono con fondo integrado
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(verde.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = verde,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rutina.nombreRutina,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Badge de tipo con fondo
                    Box(
                        modifier = Modifier
                            .background(colorTipo.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = textoTipo,
                            color = colorTipo,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    rutina.descripcion?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Info chips
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rutina.cantidadDias?.let {
                            MiniChip(texto = "$it días")
                        }
                    }
                }

                // Flecha indicadora final
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MiniChip(texto: String) {
    Text(
        text = texto,
        color = Color.Gray,
        fontSize = 11.sp,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.07f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}