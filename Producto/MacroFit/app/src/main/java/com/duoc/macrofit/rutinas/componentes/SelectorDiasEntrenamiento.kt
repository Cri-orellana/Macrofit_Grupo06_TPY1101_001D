package com.duoc.macrofit.rutinas.componentes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@Composable
fun SelectorDiasEntrenamiento(
    cantidadDias: Int,
    onDisminuir: () -> Unit,
    onAumentar: () -> Unit,
    colorFondo: Color = Color(0xFF1A1A1A),
    verde: Color = Color(0xFF76E320)
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Días de entrenamiento",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "¿Cuántos días tendrá esta rutina?",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BotonCircular(
                    texto = "−",
                    habilitado = cantidadDias > 1,
                    onClick = onDisminuir,
                    verde = verde
                )

                Text(
                    text = "$cantidadDias",
                    color = verde,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )

                BotonCircular(
                    texto = "+",
                    habilitado = cantidadDias < 7,
                    onClick = onAumentar,
                    verde = verde
                )
            }
        }
    }
}

@Composable
private fun BotonCircular(
    texto: String,
    habilitado: Boolean,
    onClick: () -> Unit,
    verde: Color
) {
    val colorBorde = if (habilitado) verde else Color(0xFF333333)
    val colorTexto = if (habilitado) verde else Color(0xFF555555)

    Box(
        modifier = Modifier
            .size(34.dp)
            .background(
                color = if (habilitado) verde.copy(alpha = 0.12f) else Color.Transparent,
                shape = androidx.compose.foundation.shape.CircleShape
            )
            .border(1.dp, colorBorde, androidx.compose.foundation.shape.CircleShape)
            .clickable(enabled = habilitado, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, color = colorTexto, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}