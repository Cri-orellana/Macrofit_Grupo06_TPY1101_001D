package com.duoc.macrofit.rutinas.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.macrofit.rutinas.model.RutinaUsuarioHistorialDTO

@Composable
fun HistorialRutinaCard(
    item: RutinaUsuarioHistorialDTO,
    colorFondo: Color = Color(0xFF1E1E1E)
) {
    val verde = Color(0xFF76E320)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.nombreRutina ?: "Sin nombre",
                    color = if (item.activo) verde else Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${item.fechaInicio} → ${item.fechaFin ?: "actual"}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            if (item.activo) {
                Text(
                    text = "Activa",
                    color = verde,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            color = verde.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}