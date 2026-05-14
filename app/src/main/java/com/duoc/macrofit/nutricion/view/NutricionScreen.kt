package com.duoc.macrofit.nutricion.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.nutricion.model.ComidaRecomendada
import com.duoc.macrofit.nutricion.viewmodel.NutricionViewModel

@Composable
fun NutricionScreen(viewModel: NutricionViewModel = viewModel()) {

    // Variables para controlar lo que el usuario escribe y el teclado
    var textoBusqueda by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tu Plan Nutricional",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "¿Qué ingredientes tienes hoy?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Barra de Búsqueda Inteligente
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = { textoBusqueda = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ej: pollo, arroz, tomate...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar ingrediente", tint = MaterialTheme.colorScheme.primary)
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    val dietaActual = viewModel.dietaSeleccionada?.nombre_tipo ?: ""

                    viewModel.buscarRecomendacionesInteligentes(
                        dieta = dietaActual,
                        ingredienteBuscado = textoBusqueda
                    )
                    keyboardController?.hide()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            ),
            shape = MaterialTheme.shapes.medium,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Selector de Dietas (Horizontal)
        if (viewModel.cargando && viewModel.listaTiposDieta.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.listaTiposDieta) { dieta ->
                    val seleccionado = viewModel.dietaSeleccionada == dieta
                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            viewModel.seleccionarDietaYBuscarComidas(dieta)

                            viewModel.buscarRecomendacionesInteligentes(
                                dieta = dieta.nombre_tipo,
                                ingredienteBuscado = textoBusqueda
                            )
                        },
                        label = { Text(dieta.nombre_tipo) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mostrar mensaje de error si la conexión falla
        viewModel.mensajeError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Lista de Comidas (Vertical)
        if (viewModel.cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.listaComidas.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(viewModel.listaComidas) { comida ->
                    TarjetaComida(comida)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Busca un ingrediente o selecciona una categoría.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// (Tu función TarjetaComida y EtiquetaMacro se mantienen exactamente iguales abajo de esto)

// Componente visual para cada plato
@Composable
fun TarjetaComida(comida: ComidaRecomendada) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = comida.nombre_comida,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comida.descripcion_comida ?: "Receta recomendada según tus requerimientos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Fila de Macronutrientes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                EtiquetaMacro("Cal", "${comida.calorias_porcion}")
                EtiquetaMacro("Pro", "${comida.proteina_porcion}g")
                EtiquetaMacro("Carb", "${comida.carbohidratos_porcion}g")
                EtiquetaMacro("Gra", "${comida.grasa_porcion}g")
            }
        }
    }
}

// Badge para los números
@Composable
fun EtiquetaMacro(titulo: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
