package com.duoc.macrofit.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = viewModel(),
    onRegistroExitoso: () -> Unit,
    onVolverAlLogin: () -> Unit
) {
    if (viewModel.registroExitoso) {
        LaunchedEffect(Unit) { onRegistroExitoso() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (viewModel.pasoActual > 1) viewModel.retrocederPaso() else onVolverAlLogin()
            }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = MaterialTheme.colorScheme.onBackground)
            }
            LinearProgressIndicator(
                progress = viewModel.pasoActual / 4f,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text("${viewModel.pasoActual}/4", color = MaterialTheme.colorScheme.onBackground)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (viewModel.pasoActual) {
                1 -> PasoCuenta(viewModel)
                2 -> PasoFisico(viewModel)
                3 -> PasoObjetivo(viewModel)
                4 -> PasoActividad(viewModel)
            }
        }

        viewModel.errorMessage?.let { error ->
            Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = { viewModel.avanzarPaso() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {
                Text(
                    text = if (viewModel.pasoActual == 4) "Finalizar Registro" else "Siguiente",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Composable
fun PasoCuenta(viewModel: RegistroViewModel) {
    Column {
        Text("Tus credenciales", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Text("¿Cómo te llamaremos en la app?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.nombre, onValueChange = { viewModel.nombre = it },
            label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.correo, onValueChange = { viewModel.correo = it },
            label = { Text("Correo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.contrasena, onValueChange = { viewModel.contrasena = it },
            label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasoFisico(viewModel: RegistroViewModel) {
    Column {
        Text("Tu cuerpo", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Text("Necesitamos estos datos para calcular tu metabolismo base.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.peso, onValueChange = { viewModel.peso = it },
            label = { Text("Peso en KG (ej: 75.5)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = viewModel.altura, onValueChange = { viewModel.altura = it },
            label = { Text("Altura en CM (ej: 175)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasoObjetivo(viewModel: RegistroViewModel) {
    Column {
        Text("¿Cuál es tu meta?", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.listaObjetivos) { objetivo ->
                val seleccionado = viewModel.objetivoSeleccionado == objetivo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.objetivoSeleccionado = objetivo },
                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionado) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (seleccionado) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Text(
                        text = objetivo.nom_objetivo,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PasoActividad(viewModel: RegistroViewModel) {
    Column {
        Text("¿Qué tan activo eres?", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.listaActividades) { actividad ->
                val seleccionado = viewModel.actividadSeleccionada == actividad
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.actividadSeleccionada = actividad },
                    colors = CardDefaults.cardColors(
                        containerColor = if (seleccionado) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                    ),
                    border = if (seleccionado) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Text(
                        text = actividad.nombre_actividad,
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}