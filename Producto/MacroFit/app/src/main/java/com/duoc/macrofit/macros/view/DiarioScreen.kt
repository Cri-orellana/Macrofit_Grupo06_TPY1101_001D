package com.duoc.macrofit.macros.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.ui.window.Dialog
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.macros.model.ComidaDto // <-- IMPORTACIÓN CORREGIDA A DTO
import com.duoc.macrofit.macros.model.Nutriments
import com.duoc.macrofit.macros.utils.BarcodeScannerView
import com.duoc.macrofit.macros.utils.OCRScannerView
import com.duoc.macrofit.macros.viewmodel.ComidaAgregada
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

    val limiteCal by viewModel.limiteCalorias.collectAsState()
    val limitePro by viewModel.limiteProteinas.collectAsState()
    val limiteCarb by viewModel.limiteCarbohidratos.collectAsState()
    val limiteGra by viewModel.limiteGrasas.collectAsState()

    var mostrarPopup by remember { mutableStateOf(false) }
    var mostrarHistorial by remember { mutableStateOf(false) }
    var comidaAEditar by remember { mutableStateOf<ComidaAgregada?>(null) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Diario Nutricional",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Row {
                    IconButton(onClick = {
                        usuario?.id?.let { viewModel.cargarHistorial(it) }
                        mostrarHistorial = true
                    }) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }

                    Button(
                        onClick = { 
                            usuario?.id?.let { viewModel.cerrarDia(it) }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Cerrar Día", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Círculo de Calorías (Centro Superior con Progreso)
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Círculo de fondo (Gris claro)
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )

                // Círculo de progreso dinámico
                val progressValue = if (limiteCal > 0) (totalCal / limiteCal).toFloat().coerceIn(0f, 1.2f) else 0f
                CircularProgressIndicator(
                    progress = { if (progressValue > 1f) 1f else progressValue },
                    modifier = Modifier.fillMaxSize(),
                    color = if (progressValue >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${totalCal.toInt()}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (progressValue > 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "de ${limiteCal.toInt()} kcal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Macronutrientes Totales (Debajo del círculo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroBox("Proteínas", totalPro, limitePro, Color(0xFFE57373))
                MacroBox("Carbs", totalCarb, limiteCarb, Color(0xFF81C784))
                MacroBox("Grasas", totalGra, limiteGra, Color(0xFFFFB74D))
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
                Spacer(modifier = Modifier.width(32.dp))
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
                            IconButton(
                                onClick = { comidaAEditar = comida },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
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

    if (mostrarHistorial) {
        DialogHistorial(
            viewModel = viewModel,
            onDismiss = { mostrarHistorial = false }
        )
    }

    if (comidaAEditar != null) {
        DialogEditarAlimento(
            comida = comidaAEditar!!,
            onDismiss = { comidaAEditar = null },
            onEliminar = {
                viewModel.eliminarAlimento(comidaAEditar!!.id)
                comidaAEditar = null
            },
            onActualizar = { nuevosGramos ->
                viewModel.actualizarPorcion(comidaAEditar!!.id, nuevosGramos)
                comidaAEditar = null
            }
        )
    }
}

@Composable
fun MacroBox(label: String, value: Double, limit: Double, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = Modifier.width(110.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text("${value.toInt()} / ${limit.toInt()}g", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
            
            // Barra de progreso pequeña opcional
            val progress = if (limit > 0) (value / limit).toFloat().coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.padding(top = 4.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun DialogBusquedaAlimento(
    viewModel: SeleccionarComidaViewModel,
    onDismiss: () -> Unit,
    onAgregar: (ComidaDto, Double) -> Unit // <--- Cuidado, aquí cambia a ComidaDto
) {
    var query by remember { mutableStateOf("") }
    val resultados by viewModel.resultadosBusqueda.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var comidaSeleccionada by remember { mutableStateOf<ComidaDto?>(null) }
    var gramos by remember { mutableStateOf("") }

    // Estados para escaneo
    var mostrandoScanner by remember { mutableStateOf(false) }
    var mostrandoOCR by remember { mutableStateOf(false) }
    var mostrandoConfirmacionOCR by remember { mutableStateOf(false) }
    var codigoDetectado by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) mostrandoScanner = true
        }
    )

    if (mostrandoScanner) {
        BarcodeScannerView(
            onBarcodeDetected = { code ->
                codigoDetectado = code
                mostrandoScanner = false
                viewModel.buscarPorCodigoDeBarra(code) { resultado ->
                    if (resultado != null) comidaSeleccionada = resultado
                    else mostrandoConfirmacionOCR = true
                }
            },
            onDismiss = { mostrandoScanner = false }
        )
        return  // <-- el return corta aquí, no muestra el Dialog
    }

    if (mostrandoOCR) {
        OCRScannerView(
            onNutrimentsDetected = { nutriments ->
                mostrandoOCR = false
                val nombreOriginal = comidaSeleccionada?.nombre
                comidaSeleccionada = ComidaDto(
                    barra = codigoDetectado ?: comidaSeleccionada?.barra,
                    nombre = if (!nombreOriginal.isNullOrBlank() && nombreOriginal != "Alimento Escaneado")
                        nombreOriginal else "Alimento Escaneado",
                    nutriments = nutriments
                )
            },
            onDismiss = { mostrandoOCR = false }
        )
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Añadir Alimento", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                if (mostrandoConfirmacionOCR) {
                    Text("Producto no encontrado en la base de datos.")
                    Text("¿Deseas escanear la tabla nutricional del producto?")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { mostrandoConfirmacionOCR = false }) { Text("No") }
                        Button(onClick = {
                            mostrandoConfirmacionOCR = false
                            mostrandoOCR = true
                        }) { Text("Sí, Escanear Tabla") }
                    }
                } else if (comidaSeleccionada == null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Buscar alimento...") },
                            modifier = Modifier.weight(1f),
                            leadingIcon = {
                                IconButton(onClick = { viewModel.buscarComida(query) }) {
                                    Icon(Icons.Default.Search, contentDescription = "Buscar")
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Search
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { viewModel.buscarComida(query) }
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                                mostrandoScanner = true
                            } else {
                                launcher.launch(Manifest.permission.CAMERA)
                            }
                        }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Escanear Código", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (cargando) CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))

                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(resultados) { comida ->
                            val n = comida.nutriments
                            val cal = n?.calorias ?: 0.0
                            val p = n?.proteinas ?: 0.0
                            val c = n?.carbohidratos ?: 0.0
                            val g = n?.grasas ?: 0.0
                            
                            ListItem(
                                headlineContent = { Text(comida.nombre ?: "Desconocido", fontWeight = FontWeight.Bold) },
                                supportingContent = { 
                                    Column {
                                        Text("$cal kcal (por 100g)", color = MaterialTheme.colorScheme.primary)
                                        Text(
                                            "P: ${p.toInt()}g | C: ${c.toInt()}g | G: ${g.toInt()}g",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                modifier = Modifier.clickable { comidaSeleccionada = comida }
                            )
                            HorizontalDivider()
                        }
                    }
                } else {
                    val n = comidaSeleccionada!!.nutriments
                    val gDouble = gramos.toDoubleOrNull() ?: 0.0

                    // Cálculo en tiempo real (Base 100g)
                    val calCalc = ((n?.calorias ?: 0.0) / 100.0) * gDouble
                    val proCalc = ((n?.proteinas ?: 0.0) / 100.0) * gDouble
                    val carbCalc = ((n?.carbohidratos ?: 0.0) / 100.0) * gDouble
                    val fatCalc = ((n?.grasas ?: 0.0) / 100.0) * gDouble

                    // Permitir editar el nombre si es escaneado o para corregir
                    var nombreAlimento by remember(comidaSeleccionada) { mutableStateOf(comidaSeleccionada?.nombre ?: "") }

                    Text(
                        if (comidaSeleccionada!!.barra != null) "Producto: $nombreAlimento" else "Seleccionado: $nombreAlimento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (comidaSeleccionada!!.nombre == "Alimento Escaneado") {
                        OutlinedTextField(
                            value = nombreAlimento,
                            onValueChange = { nombreAlimento = it },
                            label = { Text("Nombre del alimento") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (gDouble > 0) "Aportes para ${gDouble.toInt()}g:" else "Aportes (por 100g):",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Calorías", fontSize = 12.sp)
                                    Text(
                                        "${String.format(Locale.US, "%.1f", if (gDouble > 0) calCalc else n?.calorias ?: 0.0)} kcal",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text("Proteína", fontSize = 12.sp)
                                    Text(
                                        "${String.format(Locale.US, "%.1f", if (gDouble > 0) proCalc else n?.proteinas ?: 0.0)}g",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text("Carbs", fontSize = 12.sp)
                                    Text(
                                        "${String.format(Locale.US, "%.1f", if (gDouble > 0) carbCalc else n?.carbohidratos ?: 0.0)}g",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column {
                                    Text("Grasas", fontSize = 12.sp)
                                    Text(
                                        "${String.format(Locale.US, "%.1f", if (gDouble > 0) fatCalc else n?.grasas ?: 0.0)}g",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Botón para re-escanear si los datos son incorrectos o están en 0
                            val datosEnCero = (n?.calorias ?: 0.0) == 0.0 && (n?.proteinas ?: 0.0) == 0.0

                            TextButton(
                                onClick = {
                                    codigoDetectado = comidaSeleccionada?.barra
                                    mostrandoOCR = true
                                },
                                modifier = Modifier.align(Alignment.End),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    if (datosEnCero) "¡Datos en 0! Escanear tabla nutricional"
                                    else "¿No concuerdan los datos? Escanear tabla",
                                    fontSize = 11.sp,
                                    color = if (datosEnCero) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = gramos,
                        onValueChange = {
                            // Solo permitir números y un punto decimal
                            if (it.isEmpty() || it.all { char -> char.isDigit() || char == '.' }) {
                                gramos = it
                            }
                        },
                        label = { Text("Gramos consumidos") },
                        placeholder = { Text("Ej: 150") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        suffix = { Text("g") }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { comidaSeleccionada = null }) { Text("Atrás") }
                        Button(
                            onClick = {
                                val g = gramos.toDoubleOrNull() ?: 0.0
                                if (g > 0) {
                                    val comidaFinal = if (nombreAlimento != (comidaSeleccionada?.nombre ?: "")) {
                                        comidaSeleccionada?.copy(nombre = nombreAlimento)
                                    } else {
                                        comidaSeleccionada
                                    }
                                    if (comidaFinal != null) onAgregar(comidaFinal, g)
                                }
                            },
                            enabled = (gramos.toDoubleOrNull() ?: 0.0) > 0
                        ) { Text("Agregar") }
                    }
                }
                if (comidaSeleccionada == null) {
                    TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) { Text("Cerrar") }
                }
            }
        }
    }
}

@Composable
fun DialogHistorial(
    viewModel: SeleccionarComidaViewModel,
    onDismiss: () -> Unit
) {
    val historial by viewModel.historial.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Historial de Comidas", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                if (historial.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No hay historial registrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(historial) { comida ->
                            ListItem(
                                headlineContent = { Text(comida.nombre) },
                                supportingContent = {
                                    Column {
                                        Text("${comida.calorias.toInt()} kcal | ${comida.porcion.toInt()}g")
                                        comida.fecha?.let {
                                            val fechaFormateada = try {
                                                // Asumiendo formato YYYY-MM-DD o similar del backend
                                                val partes = it.split("T")[0].split("-")
                                                if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else it
                                            } catch (e: Exception) { it }

                                            Text("Fecha: $fechaFormateada", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                },
                                trailingContent = {
                                    Text(
                                        "${comida.proteinas.toInt()}/${comida.carbohidratos.toInt()}/${comida.grasas.toInt()}",
                                        fontSize = 11.sp
                                    )
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }

                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@Composable
fun DialogEditarAlimento(
    comida: ComidaAgregada,
    onDismiss: () -> Unit,
    onEliminar: () -> Unit,
    onActualizar: (Double) -> Unit
) {
    var gramos by remember { mutableStateOf(comida.porcion.toString()) }
    val gDouble = gramos.toDoubleOrNull() ?: 0.0

    val calCalc = if (comida.porcion > 0) (comida.calorias / comida.porcion) * gDouble else 0.0
    val proCalc = if (comida.porcion > 0) (comida.proteinas / comida.porcion) * gDouble else 0.0
    val carbCalc = if (comida.porcion > 0) (comida.carbohidratos / comida.porcion) * gDouble else 0.0
    val fatCalc = if (comida.porcion > 0) (comida.grasas / comida.porcion) * gDouble else 0.0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Editar Alimento", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(comida.nombre, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Nuevos aportes:",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Calorías", fontSize = 12.sp)
                                Text(String.format(Locale.US, "%.1f", calCalc), fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("P", fontSize = 12.sp)
                                Text("${String.format(Locale.US, "%.1f", proCalc)}g", fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("C", fontSize = 12.sp)
                                Text("${String.format(Locale.US, "%.1f", carbCalc)}g", fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("G", fontSize = 12.sp)
                                Text("${String.format(Locale.US, "%.1f", fatCalc)}g", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = gramos,
                    onValueChange = {
                        if (it.isEmpty() || it.all { char -> char.isDigit() || char == '.' }) {
                            gramos = it
                        }
                    },
                    label = { Text("Gramos consumidos") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text("g") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Button(
                        onClick = {
                            val g = gramos.toDoubleOrNull() ?: 0.0
                            if (g > 0) onActualizar(g)
                        },
                        enabled = gDouble > 0 && gDouble != comida.porcion
                    ) {
                        Text("Actualizar")
                    }
                }
            }
        }
    }
}
