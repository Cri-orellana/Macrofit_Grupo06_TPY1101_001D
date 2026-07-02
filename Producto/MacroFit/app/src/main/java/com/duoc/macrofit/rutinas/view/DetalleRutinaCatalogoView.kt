package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.macrofit.rutinas.componentes.EjercicioRutinaCard
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo
import com.duoc.macrofit.usuarios.utils.SessionManager

@Composable
fun DetalleRutinaCatalogoView(
    viewModel: RutinasViewModel,
    colorOscuro: Color,
    onEditarRutina: (Rutina) -> Unit
) {
    val rutina = viewModel.rutinaSeleccionadaCatalogo!!
    val idUsuarioActual = SessionManager.usuarioActual?.id
    val esRutinaPropia = rutina.idUsuarioCreador == idUsuarioActual
    val colorTema = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        MacroFitHeaderLogo()

        // Encabezado integrado
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { viewModel.rutinaSeleccionadaCatalogo = null }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column {
                Text(
                    text = rutina.nombreRutina,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (esRutinaPropia) "Rutina personal" else "Catálogo general",
                    color = if (esRutinaPropia) colorTema else Color(0xFF64B5F6),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tarjeta de descripción e información integrada
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colorOscuro),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                rutina.descripcion?.let {
                    Text(it, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(Modifier.height(12.dp))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rutina.cantidadDias?.let {
                        InfoChip(
                            icono = Icons.Default.CalendarToday,
                            texto = "$it días",
                            color = colorTema
                        )
                    }
                    InfoChip(
                        icono = Icons.Default.FitnessCenter,
                        texto = "${viewModel.ejerciciosCatalogo.size} ejercicios",
                        color = colorTema
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        viewModel.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Lógica de lista y carga
        if (viewModel.cargando) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = colorTema)
            }
        } else if (viewModel.ejerciciosCatalogo.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("Esta rutina no tiene ejercicios aún.", color = Color.Gray)
                }
            }
        } else {
            // Lógica de agrupación por día integrada
            val ejerciciosPorDia = viewModel.obtenerEjerciciosPorDia(viewModel.ejerciciosCatalogo)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ejerciciosPorDia.forEach { (dia, ejerciciosDelDia) ->
                    item(key = "titulo_dia_$dia") {
                        Column {
                            Text(
                                text = "Día $dia",
                                color = colorTema,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(6.dp))
                            HorizontalDivider(color = colorTema.copy(alpha = 0.35f))
                        }
                    }

                    itemsIndexed(
                        items = ejerciciosDelDia,
                        key = { index, item ->
                            item.parametros.idRutinaEjercicio
                                ?: "${item.parametros.idEjercicio}_${item.parametros.dia}_$index"
                        }
                    ) { index, item ->
                        EjercicioRutinaCard(numero = index + 1, item = item, colorOscuro = colorOscuro)
                    }
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones del código base (manda)
        Button(
            onClick = { viewModel.asignarRutina(rutina.idRutina) },
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

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                viewModel.prepararEdicionRutina(rutina) { rutinaEditable ->
                    onEditarRutina(rutinaEditable)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            enabled = !viewModel.preparandoEdicion,
            border = BorderStroke(1.dp, colorTema)
        ) {
            if (viewModel.preparandoEdicion) {
                CircularProgressIndicator(
                    color = colorTema,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = colorTema,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Editar rutina",
                    color = colorTema
                )
            }
        }

        if (esRutinaPropia) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = {
                    viewModel.eliminarRutinaPersonal(rutina.idRutina)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Eliminar rutina", color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoChip(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(5.dp))
        Text(texto, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}