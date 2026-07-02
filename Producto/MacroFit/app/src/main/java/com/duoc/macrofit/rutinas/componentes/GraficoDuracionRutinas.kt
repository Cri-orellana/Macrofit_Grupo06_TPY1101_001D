package com.duoc.macrofit.rutinas.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.duoc.macrofit.rutinas.model.RutinaUsuarioHistorialDTO
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun GraficoDuracionRutinas(
    historial: List<RutinaUsuarioHistorialDTO>,
    colorFondo: Color = Color(0xFF1E1E1E)
) {
    val entradas = historial.mapIndexed { index, ru ->
        val inicio = LocalDate.parse(ru.fechaInicio)
        val fin = ru.fechaFin?.let { LocalDate.parse(it) } ?: LocalDate.now()
        BarEntry(index.toFloat(), maxOf(ChronoUnit.DAYS.between(inicio, fin), 1L).toFloat())
    }

    val nombres = historial.map {
        val nombre = it.nombreRutina ?: "?"
        if (nombre.length > 8) nombre.take(8) + "…" else nombre
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Días por rutina",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            AndroidView(
                factory = { context ->
                    BarChart(context).apply {
                        description.isEnabled = false
                        legend.isEnabled = false
                        setDrawGridBackground(false)
                        setBackgroundColor(android.graphics.Color.TRANSPARENT)
                        setExtraBottomOffset(16f)

                        xAxis.apply {
                            setDrawGridLines(false)
                            textColor = android.graphics.Color.WHITE
                            textSize = 10f
                            position = XAxis.XAxisPosition.BOTTOM
                            granularity = 1f
                            labelRotationAngle = -25f
                        }

                        axisLeft.apply {
                            textColor = android.graphics.Color.WHITE
                            gridColor = android.graphics.Color.argb(40, 255, 255, 255)
                        }

                        axisRight.isEnabled = false
                    }
                },
                update = { chart ->
                    chart.xAxis.valueFormatter = IndexAxisValueFormatter(nombres)
                    chart.xAxis.labelCount = nombres.size

                    val dataSet = BarDataSet(entradas, "Días").apply {
                        colors = historial.map { ru ->
                            if (ru.activo) android.graphics.Color.parseColor("#76E320")
                            else android.graphics.Color.parseColor("#444444")
                        }
                        valueTextColor = android.graphics.Color.WHITE
                        valueTextSize = 10f
                    }
                    chart.data = BarData(dataSet)
                    chart.invalidate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
        }
    }
}