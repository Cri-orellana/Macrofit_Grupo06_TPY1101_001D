package com.duoc.macrofit.rutinas.view

import EjercicioRutinaCard
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo
import com.duoc.macrofit.rutinas.model.Rutina
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.*
import com.duoc.macrofit.usuarios.utils.SessionManager

@Composable
fun DetalleRutinaCatalogoView(
    viewModel: RutinasViewModel,
    colorOscuro: Color,
    onEditarRutina: (Rutina) -> Unit
) {
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
                text = rutina.nombreRutina,
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
                    EjercicioRutinaCard(numero = index + 1, item = item, colorOscuro = colorOscuro)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón asignar
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
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            if (viewModel.preparandoEdicion) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Editar rutina",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        val idUsuarioActual = SessionManager.usuarioActual?.id
        val esRutinaPropia = rutina.idUsuarioCreador == idUsuarioActual

        if (esRutinaPropia) {
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