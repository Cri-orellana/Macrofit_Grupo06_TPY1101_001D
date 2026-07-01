package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun HistorialRutinasView(
    viewModel: RutinasViewModel,
    colorOscuro: Color
) {
    val historial = viewModel.historialRutinas

    val entradas = historial.mapIndexed { index, ru ->
        val inicio = LocalDate.parse(ru.fechaInicio)
        val fin = ru.fechaFin?.let { LocalDate.parse(it) } ?: LocalDate.now()
        BarEntry(index.toFloat(), ChronoUnit.DAYS.between(inicio, fin).toFloat())
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Rutinas usadas: ${historial.size}",
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Días por rutina",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    description.isEnabled = false
                    legend.isEnabled = false
                    setDrawGridBackground(false)
                    xAxis.setDrawGridLines(false)
                    axisRight.isEnabled = false
                }
            },
            update = { chart ->
                val dataSet = BarDataSet(entradas, "Días activa").apply {
                    color = android.graphics.Color.parseColor("#BB86FC")
                    valueTextColor = android.graphics.Color.WHITE
                }
                chart.data = BarData(dataSet)
                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Historial",
            color = Color.White,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(historial) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(colorOscuro)
                        .padding(12.dp)
                ) {
                    Text(item.nombreRutina ?: "Sin nombre", color = Color.White)
                    Text(
                        "${item.fechaInicio} → ${item.fechaFin ?: "actual"}",
                        color = Color.Gray
                    )
                    if (item.activo) Text("Activa", color = Color.Green)
                }
            }
        }
    }
}