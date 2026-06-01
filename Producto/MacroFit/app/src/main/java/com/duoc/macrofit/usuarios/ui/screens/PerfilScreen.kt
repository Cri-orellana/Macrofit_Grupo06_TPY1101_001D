package com.duoc.macrofit.usuarios.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.usuarios.utils.SessionManager
import com.duoc.macrofit.usuarios.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(viewModel: PerfilViewModel = viewModel()) {

    var usuarioEnPantalla by remember { mutableStateOf(SessionManager.usuarioActual) }
    var mostrarPopup by remember { mutableStateOf(false) }

    val mapaActividad = mapOf(1 to "Sedentario", 2 to "Ligero", 3 to "Moderado", 4 to "Muy Activo")
    val mapaObjetivos = mapOf(1 to "Perder Grasa", 2 to "Mantener Peso", 3 to "Ganar Masa")

    val colorTarjetaOscura = Color(0xFF1A1A1A)

    if (usuarioEnPantalla == null) {
        MacroFitFondoUniversal {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay sesión activa.", color = Color.White)
            }
        }
        return
    }

    MacroFitFondoUniversal {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            MacroFitHeaderLogo()

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = usuarioEnPantalla?.nom_usuario ?: "Usuario",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = usuarioEnPantalla?.correo ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorTarjetaOscura),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tus Calorías Diarias", style = MaterialTheme.typography.titleMedium, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${usuarioEnPantalla?.cal_diaria?.toInt() ?: 0} kcal",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.DarkGray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TMB (Metabolismo Basal)", color = Color.LightGray)
                        Text("${usuarioEnPantalla?.tmb_objetivo?.toInt() ?: 0} kcal", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = colorTarjetaOscura),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    FilaDato("Edad", "${usuarioEnPantalla?.edad ?: 0} años")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)
                    FilaDato("Peso", "${usuarioEnPantalla?.peso ?: 0f} kg")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)
                    FilaDato("Altura", "${usuarioEnPantalla?.altura ?: 0} cm")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)
                    FilaDato("Actividad", mapaActividad[usuarioEnPantalla?.id_nv_act] ?: "N/A")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.DarkGray)
                    FilaDato("Objetivo", mapaObjetivos[usuarioEnPantalla?.id_objetivo] ?: "N/A")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { mostrarPopup = true },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (viewModel.cargando) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.padding(end = 8.dp))
                    Text("Editar Perfil")
                }
            }

            viewModel.mensajeError?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { SessionManager.limpiarSesion() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Filled.ExitToApp, contentDescription = "Salir", modifier = Modifier.padding(end = 8.dp))
                Text("Cerrar Sesión")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (mostrarPopup) {
                PopupEditar(
                    usuarioEnPantalla = usuarioEnPantalla!!,
                    alCerrar = { mostrarPopup = false },
                    alGuardar = { nuevoNombre, nuevoCorreo, nuevaEdad, nuevoPeso, nuevaAltura, nuevaActividad, nuevoObjetivo ->
                        viewModel.actualizarPerfilServidor(
                            nuevoNombre, nuevoCorreo, nuevaEdad, nuevoPeso, nuevaAltura, nuevaActividad, nuevoObjetivo
                        ) { usuarioActualizado ->
                            usuarioEnPantalla = usuarioActualizado
                            mostrarPopup = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilaDato(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = etiqueta, color = Color.LightGray)
        Text(text = valor, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupEditar(
    usuarioEnPantalla: com.duoc.macrofit.usuarios.model.Usuario,
    alCerrar: () -> Unit,
    alGuardar: (String, String, Int, Float, Int, Int, Int) -> Unit
) {
    var campoNombre by remember { mutableStateOf(usuarioEnPantalla.nom_usuario) }
    var campoCorreo by remember { mutableStateOf(usuarioEnPantalla.correo) }
    var campoEdad by remember { mutableStateOf(usuarioEnPantalla.edad?.toString() ?: "") }
    var campoPeso by remember { mutableStateOf(usuarioEnPantalla.peso.toString()) }
    var campoAltura by remember { mutableStateOf(usuarioEnPantalla.altura?.toString() ?: "") }

    var expandidoActividad by remember { mutableStateOf(false) }
    var seleccionadoActividad by remember { mutableStateOf(usuarioEnPantalla.id_nv_act ?: 2) }
    val opcionesActividad = mapOf(1 to "Sedentario", 2 to "Ligero", 3 to "Moderado", 4 to "Muy Activo")

    var expandidoObjetivo by remember { mutableStateOf(false) }
    var seleccionadoObjetivo by remember { mutableStateOf(usuarioEnPantalla.id_objetivo ?: 2) }
    val opcionesObjetivo = mapOf(1 to "Perder Grasa", 2 to "Mantener", 3 to "Ganar Masa")

    AlertDialog(
        onDismissRequest = alCerrar,
        title = { Text("Actualizar Perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(value = campoNombre, onValueChange = { campoNombre = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(value = campoCorreo, onValueChange = { campoCorreo = it }, label = { Text("Correo") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = campoEdad, onValueChange = { campoEdad = it }, label = { Text("Edad") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = campoPeso, onValueChange = { campoPeso = it }, label = { Text("Peso (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                }

                OutlinedTextField(value = campoAltura, onValueChange = { campoAltura = it }, label = { Text("Altura (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                ExposedDropdownMenuBox(expanded = expandidoActividad, onExpandedChange = { expandidoActividad = !expandidoActividad }) {
                    OutlinedTextField(value = opcionesActividad[seleccionadoActividad] ?: "", onValueChange = {}, readOnly = true, label = { Text("Nivel de Actividad") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoActividad) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandidoActividad, onDismissRequest = { expandidoActividad = false }) {
                        opcionesActividad.forEach { (id, nombre) -> DropdownMenuItem(text = { Text(nombre) }, onClick = { seleccionadoActividad = id; expandidoActividad = false }) }
                    }
                }

                ExposedDropdownMenuBox(expanded = expandidoObjetivo, onExpandedChange = { expandidoObjetivo = !expandidoObjetivo }) {
                    OutlinedTextField(value = opcionesObjetivo[seleccionadoObjetivo] ?: "", onValueChange = {}, readOnly = true, label = { Text("Objetivo") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoObjetivo) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                    ExposedDropdownMenu(expanded = expandidoObjetivo, onDismissRequest = { expandidoObjetivo = false }) {
                        opcionesObjetivo.forEach { (id, nombre) -> DropdownMenuItem(text = { Text(nombre) }, onClick = { seleccionadoObjetivo = id; expandidoObjetivo = false }) }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                alGuardar(campoNombre, campoCorreo, campoEdad.toIntOrNull() ?: usuarioEnPantalla.edad ?: 0, campoPeso.toFloatOrNull() ?: usuarioEnPantalla.peso, campoAltura.toIntOrNull() ?: usuarioEnPantalla.altura ?: 0, seleccionadoActividad, seleccionadoObjetivo)
            }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = alCerrar) { Text("Cancelar") } }
    )
}