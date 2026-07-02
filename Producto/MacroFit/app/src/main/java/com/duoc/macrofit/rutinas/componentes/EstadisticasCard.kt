package com.duoc.macrofit.rutinas.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EstadisticaCard(
    titulo: String,
    valor: String,
    subtitulo: String? = null,
    colorValor: Color = Color(0xFF76E320),
    colorFondo: Color = Color(0xFF1E1E1E),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(titulo, color = Color.Gray, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = valor,
                color = colorValor,
                fontSize = if (valor.length > 10) 16.sp else 32.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitulo != null) {
                Spacer(Modifier.height(2.dp))
                Text(subtitulo, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}