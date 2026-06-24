import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.componentes.ChipParametro
import com.duoc.macrofit.rutinas.componentes.YoutubeVideoPlayer
import com.duoc.macrofit.rutinas.viewmodel.EjercicioEnRutina

@Composable
fun EjercicioRutinaCard(numero: Int, item: EjercicioEnRutina, colorOscuro: Color) {
    val re = item.parametros
    val ej = item.detalle

    var expandido by remember { mutableStateOf(false) }
    var mostrarVideo by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expandido = !expandido },
        colors = CardDefaults.cardColors(containerColor = colorOscuro),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            // Mantiene el número centrado si está cerrado, o arriba si está expandido
            verticalAlignment = if (expandido) Alignment.Top else Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Número de orden
            Box(
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$numero",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                // Título y flecha de expansión
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ej?.nombreEjercicio ?: "Ejercicio #${re.idEjercicio}",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expandido) "Contraer" else "Expandir",
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Chips de parámetros
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    re.series?.let {
                        ChipParametro(label = "$it series")
                    }
                    re.repeticiones?.let {
                        ChipParametro(label = "$it reps")
                    }
                    re.tiempoSeg?.let {
                        ChipParametro(label = "${it}s")
                    }
                    re.pesoReferencia?.let {
                        if (it > 0f) ChipParametro(label = "${it.toInt()} kg")
                    }
                }

                // Descripción (2 líneas si está cerrado, completo si está abierto)
                ej?.descripcion?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = if (expandido) Int.MAX_VALUE else 2
                    )
                }

                // Lógica y renderizado del video (Solo visible al expandir)
                if (expandido) {
                    Spacer(modifier = Modifier.height(10.dp))

                    if (!ej?.videoEjercicio.isNullOrBlank()) {
                        OutlinedButton(
                            onClick = { mostrarVideo = !mostrarVideo },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = if (mostrarVideo) "Ocultar video" else "Ver video",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (mostrarVideo) {
                            Spacer(modifier = Modifier.height(12.dp))

                            YoutubeVideoPlayer(
                                videoUrl = ej?.videoEjercicio,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Este ejercicio no tiene video disponible.",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}