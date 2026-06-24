package com.duoc.macrofit.rutinas.view

import EjercicioRutinaCard
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo

@Composable
fun RutinaActivaView(viewModel: RutinasViewModel, colorOscuro: Color, onCrearRutina: () -> Unit, onEditarRutina: (Rutina) -> Unit) {
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
                            text = rutina.nombreRutina,
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
                    val ejerciciosPorDia = viewModel.obtenerEjerciciosPorDia()

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ejerciciosPorDia.forEach { (dia, ejerciciosDelDia) ->

                            item(key = "titulo_dia_$dia") {
                                Column {
                                    Text(
                                        text = "Día $dia",
                                        color = MaterialTheme.colorScheme.primary,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                                    )
                                }
                            }

                            itemsIndexed(
                                items = ejerciciosDelDia,
                                key = { index, item ->
                                    item.parametros.idRutinaEjercicio ?: "${item.parametros.idEjercicio}_${item.parametros.dia}_$index"
                                }
                            ) { index, item ->
                                EjercicioRutinaCard(
                                    numero = index + 1,
                                    item = item,
                                    colorOscuro = colorOscuro
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                Text("Mis Rutinas", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                viewModel.rutinaActiva?.let { rutina ->
                    onEditarRutina(rutina)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Editar Rutina", color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { onCrearRutina() },      // pasar este lambda desde RutinasScreen
            modifier = Modifier.fillMaxWidth().height(50.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Crear Rutina Personalizada", color = Color.LightGray)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}