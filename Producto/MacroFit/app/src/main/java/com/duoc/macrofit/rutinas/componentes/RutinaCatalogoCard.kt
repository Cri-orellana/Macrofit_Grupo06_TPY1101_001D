package com.duoc.macrofit.rutinas.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.usuarios.utils.SessionManager

@Composable
fun RutinaCatalogoCard(
    rutina: Rutina,
    colorOscuro: Color,
    onClick: () -> Unit
) {
    val idUsuarioActual = SessionManager.usuarioActual?.id

    val esRutinaPropia = rutina.idUsuarioCreador == idUsuarioActual
    val esRutinaCatalogo = rutina.activoCatalogo == true

    val textoTipo = when {
        esRutinaPropia -> "Rutina personal"
        esRutinaCatalogo -> "Rutina del catálogo"
        else -> "Rutina"
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
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rutina.nombreRutina,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = textoTipo,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )

                    rutina.descripcion?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            color = Color.LightGray,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}