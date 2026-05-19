package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.rutinas.viewmodel.EjercicioEnRutina
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitFondoUniversal
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo

@Composable
fun RutinasScreen(viewModel: RutinasViewModel = viewModel()) {

    val colorOscuro = Color(0xFF1A1A1A)

    MacroFitFondoUniversal {
        when {
            viewModel.mostrarCatalogo && viewModel.rutinaSeleccionadaCatalogo != null -> {
                // Vista 3: Detalle de una rutina del catálogo
                DetalleRutinaCatalogoView(viewModel = viewModel, colorOscuro = colorOscuro)
            }
            viewModel.mostrarCatalogo -> {
                // Vista 2: Lista del catálogo
                CatalogoRutinasView(viewModel = viewModel, colorOscuro = colorOscuro)
            }
            else -> {
                // Vista 1: Rutina activa del usuario
                RutinaActivaView(viewModel = viewModel, colorOscuro = colorOscuro)
            }
        }
    }
}

// ─────────────────────────────────────────
// VISTA 1: Rutina activa del usuario
// ─────────────────────────────────────────
@Composable
private fun RutinaActivaView(viewModel: RutinasViewModel, colorOscuro: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MacroFitHeaderLogo()
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Mi Rutina",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensaje de error
        viewModel.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
        }

        when {
            viewModel.cargando -> {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            viewModel.rutinaActiva == null -> {
                // Sin rutina activa
                SinRutinaView(
                    modifier = Modifier.weight(1f),
                    onBrowse = { viewModel.abrirCatalogo() }
                )
            }
            else -> {
                // Mostrar rutina activa
                val rutina = viewModel.rutinaActiva!!

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorOscuro),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = rutina.nombre_rutina,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        rutina.descripcion?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(it, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${viewModel.ejerciciosEnRutina.size} ejercicios",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (viewModel.ejerciciosEnRutina.isEmpty() && !viewModel.cargando) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No hay ejercicios en esta rutina.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(viewModel.ejerciciosEnRutina) { index, item ->
                            TarjetaEjercicio(numero = index + 1, item = item, colorOscuro = colorOscuro)
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón cambiar rutina (siempre visible)
        if (!viewModel.cargando) {
            OutlinedButton(
                onClick = { viewModel.abrirCatalogo() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    Icons.Default.LibraryBooks,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Cambiar Rutina", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ─────────────────────────────────────────
// VISTA 2: Catálogo de rutinas
// ─────────────────────────────────────────
@Composable
private fun CatalogoRutinasView(viewModel: RutinasViewModel, colorOscuro: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        MacroFitHeaderLogo()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { viewModel.cerrarCatalogo() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                text = "Catálogo de Rutinas",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (viewModel.rutinasDisponibles.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(viewModel.rutinasDisponibles) { _, rutina ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.verDetallesRutinaCatalogo(rutina) },
                        colors = CardDefaults.cardColors(containerColor = colorOscuro),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = rutina.nombre_rutina,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    rutina.descripcion?.let {
                                        Text(
                                            text = it,
                                            color = Color.LightGray,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────
// VISTA 3: Detalle de rutina del catálogo
// ─────────────────────────────────────────
@Composable
private fun DetalleRutinaCatalogoView(viewModel: RutinasViewModel, colorOscuro: Color) {
    val rutina = viewModel.rutinaSeleccionadaCatalogo!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        MacroFitHeaderLogo()

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { viewModel.rutinaSeleccionadaCatalogo = null }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text(
                text = rutina.nombre_rutina,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }

        rutina.descripcion?.let {
            Text(
                text = it,
                color = Color.LightGray,
                modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        viewModel.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (viewModel.cargando) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (viewModel.ejerciciosCatalogo.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Esta rutina no tiene ejercicios aún.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(viewModel.ejerciciosCatalogo) { index, item ->
                    TarjetaEjercicio(numero = index + 1, item = item, colorOscuro = colorOscuro)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón asignar
        Button(
            onClick = { viewModel.asignarRutina(rutina.id_rutina) },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            enabled = !viewModel.asignandoRutina
        ) {
            if (viewModel.asignandoRutina) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Usar esta rutina",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ─────────────────────────────────────────
// COMPOSABLES DE APOYO
// ─────────────────────────────────────────

@Composable
private fun TarjetaEjercicio(numero: Int, item: EjercicioEnRutina, colorOscuro: Color) {
    val re = item.parametros
    val ej = item.detalle

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorOscuro),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                Text(
                    text = ej?.nombre_ejercicio ?: "Ejercicio #${re.id_ejercicio}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Chips de parámetros
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    re.series?.let {
                        ChipParametro(label = "$it series")
                    }
                    re.repeticiones?.let {
                        ChipParametro(label = "$it reps")
                    }
                    re.tiempo_seg?.let {
                        ChipParametro(label = "${it}s")
                    }
                    re.peso_referencia?.let {
                        if (it > 0f) ChipParametro(label = "${it.toInt()} kg")
                    }
                }

                ej?.descripcion?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = it,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun ChipParametro(label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SinRutinaView(modifier: Modifier = Modifier, onBrowse: () -> Unit) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Todavía no tienes una rutina activa",
            color = Color.LightGray,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Elige una del catálogo para comenzar",
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onBrowse, modifier = Modifier.height(50.dp)) {
            Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Ver Catálogo")
        }
    }
}