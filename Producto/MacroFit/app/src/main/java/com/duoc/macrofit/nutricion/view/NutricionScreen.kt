package com.duoc.macrofit.nutricion.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.duoc.macrofit.nutricion.model.ComidaRecomendada
import com.duoc.macrofit.nutricion.viewmodel.NutricionViewModel
import com.duoc.macrofit.macros.viewmodel.SeleccionarComidaViewModel
import com.duoc.macrofit.usuarios.utils.SessionManager

@Composable
fun NutricionScreen(
    viewModel: NutricionViewModel = viewModel(),
    macrosViewModel: SeleccionarComidaViewModel = viewModel()
    ) {

    // Variables para controlar lo que el usuario escribe y el teclado
    var textoBusqueda by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Variable para controlar qué receta se muestra en el Popup
    var recetaSeleccionada by remember { mutableStateOf<ComidaRecomendada?>(null) }

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
                    val texto = textoBusqueda.trim()
                    if (texto.isEmpty()) {
                        viewModel.limpiarFiltro()
                    } else {
                        viewModel.buscarRecomendacionesInteligentes(
                            ingredienteBuscado = texto
                        )
                    }
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

        // Selector de Dietas
        if (viewModel.cargando && viewModel.listaTiposDieta.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(viewModel.listaTiposDieta) { dieta ->
                    val seleccionado = viewModel.dietaSeleccionada == dieta
                    FilterChip(
                        selected = seleccionado,
                        onClick = {
                            if (viewModel.dietaSeleccionada == dieta) {
                                viewModel.limpiarFiltro()
                            } else {
                                viewModel.seleccionarDietaYBuscarComidas(dieta)
                            }
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

        // Lista de Comidas
        if (viewModel.cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (viewModel.listaComidas.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(viewModel.listaComidas) { comida ->
                    TarjetaComida(
                        comida = comida,
                        alHacerClick = { recetaSeleccionada = comida }
                    )
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

    recetaSeleccionada?.let { comidaSeleccionada ->
        PopupReceta(
            comida = comidaSeleccionada,
            alCerrar = { recetaSeleccionada = null },
            macrosViewModel = macrosViewModel
        )
    }
}

// Tarjeta para cada plato
@Composable
fun TarjetaComida(comida: ComidaRecomendada, alHacerClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { alHacerClick() },
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

// Badge para los números de macros
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

//Pop-up de la receta detallada
@Composable
fun PopupReceta(comida: ComidaRecomendada, alCerrar: () -> Unit, macrosViewModel: SeleccionarComidaViewModel ) {
    var agregado by remember { mutableStateOf(false) }
    val usuario = SessionManager.usuarioActual
    Dialog(
        onDismissRequest = { alCerrar() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Imagen de la comida (Usando Coil)
                if (!comida.foto_comida.isNullOrEmpty()) {
                    AsyncImage(
                        model = comida.foto_comida,
                        contentDescription = comida.nombre_comida,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Título y Gramaje
                Text(
                    text = comida.nombre_comida,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Porción: ${comida.cantidad_porcion}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Ingredientes
                Text(
                    text = "Ingredientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                comida.ingredientes_lista?.forEach { ingrediente ->
                    Text(
                        text = "• $ingrediente",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preparación
                Text(
                    text = "Preparación",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                comida.preparacion_lista?.forEachIndexed { index, paso ->
                    Text(
                        text = "${index + 1}. $paso",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        usuario?.id?.let { userId ->
                            macrosViewModel.agregarRecetaRecomendada(
                                nombre        = comida.nombre_comida,
                                calorias      = comida.calorias_porcion.toDouble(),
                                proteinas     = comida.proteina_porcion.toDouble(),
                                carbohidratos = comida.carbohidratos_porcion.toDouble(),
                                grasas        = comida.grasa_porcion.toDouble(),
                                usuarioId     = userId
                            )
                            agregado = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (agregado)
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (agregado) "✓ Añadido al diario" else "Añadir al diario")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { alCerrar() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Volver")
                }
            }
        }
    }
}
