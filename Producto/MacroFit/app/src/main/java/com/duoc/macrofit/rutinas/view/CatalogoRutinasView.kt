package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duoc.macrofit.rutinas.componentes.RutinaCatalogoCard
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo
import com.duoc.macrofit.usuarios.utils.SessionManager

@Composable
fun CatalogoRutinasView(
    viewModel: RutinasViewModel,
    colorOscuro: Color
) {
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
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }

            Column {
                Text(
                    text = "Catálogo de Rutinas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${viewModel.rutinasDisponibles.size} rutinas disponibles",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        viewModel.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (viewModel.cargando) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (viewModel.rutinasDisponibles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "No hay rutinas disponibles.",
                        color = Color.Gray
                    )
                }
            }
        } else {
            // Lógica integrada para separar personales y de catálogo
            val idUsuario = SessionManager.usuarioActual?.id
            val personales = viewModel.rutinasDisponibles.filter { it.idUsuarioCreador == idUsuario }
            val catalogo = viewModel.rutinasDisponibles.filter { it.activoCatalogo == true && it.idUsuarioCreador != idUsuario }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                if (personales.isNotEmpty()) {
                    item {
                        SeccionLabel(texto = "Mis rutinas personales", colorTema = MaterialTheme.colorScheme.primary)
                    }
                    itemsIndexed(personales) { _, rutina ->
                        RutinaCatalogoCard(
                            rutina = rutina,
                            colorOscuro = colorOscuro,
                            onClick = { viewModel.verDetallesRutinaCatalogo(rutina) }
                        )
                    }
                }

                if (catalogo.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        SeccionLabel(texto = "Catálogo general", colorTema = MaterialTheme.colorScheme.primary)
                    }
                    itemsIndexed(catalogo) { _, rutina ->
                        RutinaCatalogoCard(
                            rutina = rutina,
                            colorOscuro = colorOscuro,
                            onClick = { viewModel.verDetallesRutinaCatalogo(rutina) }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun SeccionLabel(texto: String, colorTema: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .background(colorTema, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = texto,
            color = colorTema,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}