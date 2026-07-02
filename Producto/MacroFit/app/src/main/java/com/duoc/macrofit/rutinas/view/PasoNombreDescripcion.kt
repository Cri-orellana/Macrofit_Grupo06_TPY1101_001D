package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.shape.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.componentes.SelectorDiasEntrenamiento
import com.duoc.macrofit.rutinas.viewmodel.CrearRutinaViewModel

@Composable
fun PasoNombreDescripcion(viewModel: CrearRutinaViewModel) {
    val verde = Color(0xFF76E320)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Nueva Rutina",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Dale un nombre y una descripción opcional.",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = viewModel.nombreRutina,
            onValueChange = { viewModel.nombreRutina = it },
            label = { Text("Nombre de la rutina *") },
            placeholder = { Text("Ej: Fuerza PPL Lunes", color = Color.DarkGray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = camposColors(verde),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = viewModel.descripcionRutina,
            onValueChange = { viewModel.descripcionRutina = it },
            label = { Text("Descripción (opcional)") },
            placeholder = { Text("Ej: Empuje de pecho, hombro y tríceps", color = Color.DarkGray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4,
            colors = camposColors(verde),
            shape = RoundedCornerShape(12.dp)
        )

        SelectorDiasEntrenamiento(
            cantidadDias = viewModel.cantidadDias,
            onDisminuir = { viewModel.disminuirDias() },
            onAumentar = { viewModel.aumentarDias() },
            verde = verde
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun camposColors(verde: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = verde,
    unfocusedBorderColor = Color(0xFF333333),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = verde,
    unfocusedLabelColor = Color.Gray,
    cursorColor = verde
)