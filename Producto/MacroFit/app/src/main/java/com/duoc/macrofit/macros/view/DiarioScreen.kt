package com.duoc.macrofit.macros.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.macros.model.ComidaResponse
import com.duoc.macrofit.macros.viewmodel.SeleccionarComidaViewModel
import com.duoc.macrofit.usuarios.utils.SessionManager

@Composable
fun DiarioScreen(viewModel: SeleccionarComidaViewModel = viewModel()) {
    val usuario = SessionManager.usuarioActual
    val comidas by viewModel.comidasAgregadas.collectAsState()
    val totalCal by viewModel.totalCalorias.collectAsState()
    val totalPro by viewModel.totalProteinas.collectAsState()
    val totalCarb by viewModel.totalCarbohidratos.collectAsState()
    val totalGra by viewModel.totalGrasas.collectAsState()

    var mostrarPopup by remember { mutableStateOf(false) }

    LaunchedEffect(usuario) {
        usuario?.id?.let {
            viewModel.cargarDiarioDelDia(it)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Diario Nutricional",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Círculo de Calorías (Centro Superior)
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(4.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalCal.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "kcal",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Macronutrientes Totales (Debajo del círculo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroBox("Proteínas", totalPro, Color(0xFFE57373))
                MacroBox("Carbs", totalCarb, Color(0xFF81C784))
                MacroBox("Grasas", totalGra, Color(0xFFFFB74D))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Tabla de Alimentos
            Text(
                text = "Alimentos Consumidos",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Encabezado de la Tabla
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    .padding(8.dp)
            ) {
                Text("Alimento", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Cant.", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Cal.", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("P/C/G", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                items(comidas) { comida ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(comida.nombre, modifier = Modifier.weight(2f), fontSize = 13.sp)
                            Text("${comida.porcion.toInt()}g", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            Text("${comida.calorias.toInt()}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                            Text(
                                "${comida.proteinas.toInt()}/${comida.carbohidratos.toInt()}/${comida.grasas.toInt()}",
                                modifier = Modifier.weight(1.5f),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }

        // 4. Botón Agregar (Esquina Inferior Izquierda)
        FloatingActionButton(
            onClick = { mostrarPopup = true },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Alimento")
        }
    }

    if (mostrarPopup) {
        DialogBusquedaAlimento(
            viewModel = viewModel,
            onDismiss = { mostrarPopup = false },
            onAgregar = { comida, gramos ->
                usuario?.id?.let {
                    viewModel.agregarAlimento(comida, gramos, it)
                }
                mostrarPopup = false
            }
        )
    }
}

@Composable
fun MacroBox(label: String, value: Double, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = Modifier.width(100.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("${value.toInt()}g", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun DialogBusquedaAlimento(
    viewModel: SeleccionarComidaViewModel,
    onDismiss: () -> Unit,
    onAgregar: (ComidaResponse, Double) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val resultados by viewModel.resultadosBusqueda.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    
    var comidaSeleccionada by remember { mutableStateOf<ComidaResponse?>(null) }
    var gramos by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Añadir Alimento", style = MaterialTheme.typography.titleLarge)
                
                Spacer(modifier = Modifier.height(16.dp))

                if (comidaSeleccionada == null) {
                    // Buscador
                    OutlinedTextField(
                        value = query,
                        onValueChange = { 
                            query = it
                            viewModel.buscarComida(it)
                        },
                        label = { Text("Buscar alimento...") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (cargando) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(resultados) { comida ->
                            ListItem(
                                headlineContent = { Text(comida.nombre) },
                                supportingContent = { Text("${comida.calorias} kcal (por 100g/unidad)") },
                                modifier = Modifier.clickable { comidaSeleccionada = comida }
                            )
                            HorizontalDivider()
                        }
                    }
                } else {
                    // Confirmar cantidad
                    Text("Seleccionado: ${comidaSeleccionada!!.nombre}", fontWeight = FontWeight.Bold)
                    Text("Macros: ${comidaSeleccionada!!.calorias} kcal | P: ${comidaSeleccionada!!.proteinas} C: ${comidaSeleccionada!!.carbohidratos} G: ${comidaSeleccionada!!.grasas}")
                    
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = gramos,
                        onValueChange = { gramos = it },
                        label = { Text("Gramos consumidos") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { comidaSeleccionada = null }) {
                            Text("Atrás")
                        }
                        Button(
                            onClick = { 
                                val g = gramos.toDoubleOrNull() ?: 0.0
                                if (g > 0) onAgregar(comidaSeleccionada!!, g)
                            },
                            enabled = gramos.isNotEmpty()
                        ) {
                            Text("Agregar")
                        }
                    }
                }
                
                if (comidaSeleccionada == null) {
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                        Text("Cerrar")
                    }
                }
            }
        }
    }
}
