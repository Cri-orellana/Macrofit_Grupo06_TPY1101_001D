package com.duoc.macrofit.rutinas.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.rutinas.model.Rutina
import com.duoc.macrofit.rutinas.viewmodel.EjercicioEnRutina
import com.duoc.macrofit.rutinas.viewmodel.RutinasViewModel
import com.duoc.macrofit.usuarios.ui.screens.MacroFitFondoUniversal
import com.duoc.macrofit.usuarios.ui.screens.MacroFitHeaderLogo

/**
 * Pantalla de detalle de una rutina.
 * Muestra la información de la rutina y la lista ordenada de sus ejercicios
 * con todos los parámetros (series, reps, tiempo, peso).
 *
 * Se puede usar de dos formas:
 *   1. Desde el catálogo: pasas rutina + ejercicios + acción "Usar esta rutina"
 *   2. Desde "Mi Rutina activa": pasas la rutina del usuario con su lista
 *
 * @param rutina          La rutina a mostrar.
 * @param ejercicios      Lista de ejercicios con sus parámetros.
 * @param cargando        Si se están cargando los ejercicios.
 * @param onVolver        Acción al presionar atrás.
 * @param textoBotonAccion Texto del botón principal (null = no mostrar botón).
 * @param cargandoAccion  Si la acción principal está en progreso.
 * @param onAccion        Callback del botón principal.
 */
@Composable
fun DetalleRutinaScreen(
    rutina: Rutina,
    ejercicios: List<EjercicioEnRutina>,
    cargando: Boolean = false,
    onVolver: () -> Unit,
    textoBotonAccion: String? = null,
    cargandoAccion: Boolean = false,
    onAccion: (() -> Unit)? = null,
    errorMensaje: String? = null
) {
    val colorOscuro = Color(0xFF1A1A1A)
    val verde = MaterialTheme.colorScheme.primary

    MacroFitFondoUniversal {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            MacroFitHeaderLogo()

            // ── Barra superior ────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Text(
                    text = rutina.nombreRutina,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Descripción ───────────────────────────────────────
            rutina.descripcion?.let {
                Text(
                    text = it,
                    color = Color.LightGray,
                    modifier = Modifier.padding(start = 12.dp, bottom = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // ── Badge de cantidad de ejercicios ───────────────────
            Row(
                modifier = Modifier.padding(start = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = verde,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${ejercicios.size} ejercicios",
                    color = verde,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ── Error ─────────────────────────────────────────────
            errorMensaje?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // ── Lista de ejercicios ───────────────────────────────
            when {
                cargando -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = verde)
                    }
                }
                ejercicios.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = Color.DarkGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Esta rutina no tiene ejercicios aún.", color = Color.Gray)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(ejercicios) { index, item ->
                            TarjetaEjercicioDetalle(
                                numero = index + 1,
                                item = item,
                                colorOscuro = colorOscuro
                            )
                        }
                        item { Spacer(modifier = Modifier.height(8.dp)) }
                    }
                }
            }

            // ── Botón de acción principal ─────────────────────────
            if (textoBotonAccion != null && onAccion != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAccion,
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    enabled = !cargandoAccion
                ) {
                    if (cargandoAccion) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = textoBotonAccion,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Tarjeta interna — muestra un ejercicio con todos sus parámetros
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TarjetaEjercicioDetalle(
    numero: Int,
    item: EjercicioEnRutina,
    colorOscuro: Color
) {
    val re = item.parametros
    val ej = item.detalle
    val verde = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorOscuro),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Fila superior: número + nombre
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = verde.copy(alpha = 0.15f)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "$numero",
                            fontWeight = FontWeight.Bold,
                            color = verde,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }

                Text(
                    text = ej?.nombreEjercicio ?: "Ejercicio #${re.idEjercicio}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
            }

            // Descripción del ejercicio
            ej?.descripcion?.let {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = it,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFF2A2A2A))
            Spacer(modifier = Modifier.height(10.dp))

            // Chips de parámetros
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                re.series?.let { ChipDetalle(label = "$it series") }
                re.repeticiones?.let { ChipDetalle(label = "$it reps") }
                re.tiempoSeg?.let { ChipDetalle(label = "${it}s") }
                re.pesoReferencia?.let {
                    if (it > 0f) ChipDetalle(label = "${it.toInt()} kg")
                }
            }
        }
    }
}

@Composable
private fun ChipDetalle(label: String) {
    val verde = MaterialTheme.colorScheme.primary
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = verde.copy(alpha = 0.18f)
    ) {
        Text(
            text = label,
            color = verde,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Wrapper para usar DetalleRutinaScreen desde RutinasViewModel
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Versión conectada al RutinasViewModel.
 * Usada desde el catálogo al elegir una rutina.
 */
@Composable
fun DetalleRutinaCatalogoConectado(viewModel: RutinasViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val rutina = viewModel.rutinaSeleccionadaCatalogo ?: return

    DetalleRutinaScreen(
        rutina = rutina,
        ejercicios = viewModel.ejerciciosCatalogo,
        cargando = viewModel.cargando,
        errorMensaje = viewModel.error,
        onVolver = { viewModel.rutinaSeleccionadaCatalogo = null },
        textoBotonAccion = "Usar esta rutina",
        cargandoAccion = viewModel.asignandoRutina,
        onAccion = { viewModel.asignarRutina(rutina.idRutina) }
    )
}
