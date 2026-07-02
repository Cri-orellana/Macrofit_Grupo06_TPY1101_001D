package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.duoc.macrofit.rutinas.componentes.EstadisticaCard
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo
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
    val verde = Color(0xFF76E320)

    val rutinaActiva = historial.firstOrNull { it.activo }
    val diasActivo = rutinaActiva?.let {
        ChronoUnit.DAYS.between(LocalDate.parse(it.fechaInicio), LocalDate.now())
    } ?: 0L

    val entradas = historial.mapIndexed { index, ru ->
        val inicio = LocalDate.parse(ru.fechaInicio)
        val fin = ru.fechaFin?.let { LocalDate.parse(it) } ?: LocalDate.now()
        BarEntry(index.toFloat(), ChronoUnit.DAYS.between(inicio, fin).toFloat())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // <-- SOLUCIÓN: Agregamos el scroll a toda la pantalla
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        MacroFitHeaderLogo()

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Mis Estadísticas",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EstadisticaCard(
                titulo = "Rutina activa",
                valor = rutinaActiva?.nombreRutina ?: "Ninguna",
                subtitulo = "$diasActivo días activa",
                colorValor = Color.White,
                modifier = Modifier.weight(1f)
            )
            EstadisticaCard(
                titulo = "Rutinas usadas",
                valor = "${historial.size}",
                subtitulo = "en total",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // SOLUCIÓN: Cambiamos LazyColumn por Column + forEach para evitar conflictos de scroll
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            historial.forEach { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorOscuro, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = item.nombreRutina ?: "Sin nombre",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${item.fechaInicio} → ${item.fechaFin ?: "actual"}",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                    if (item.activo) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Activa",
                            color = verde,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Espaciado final para que la lista no quede escondida detrás del menú inferior
        Spacer(modifier = Modifier.height(90.dp))
    }
}