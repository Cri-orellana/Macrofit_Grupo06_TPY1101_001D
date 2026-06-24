package com.duoc.macrofit.macros.utils

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.duoc.macrofit.macros.model.Nutriments
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerView(
    onBarcodeDetected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var lastDetectedCode by remember { mutableStateOf("") }
    var detectedCount by remember { mutableIntStateOf(0) }
    val requiredConfidence = 5

    var zoomLevel by remember { mutableFloatStateOf(0f) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraControl?.enableTorch(false)
            } catch (e: Exception) {
                Log.e("Scanner", "Error turning off torch", e)
            }
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    val scanner = BarcodeScanning.getClient()

                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    if (barcodes.isNotEmpty()) {
                                        val code = barcodes[0].rawValue ?: ""
                                        if (code == lastDetectedCode) {
                                            detectedCount++
                                            if (detectedCount >= requiredConfidence) {
                                                cameraControl?.enableTorch(false)
                                                onBarcodeDetected(code)
                                            }
                                        } else {
                                            lastDetectedCode = code
                                            detectedCount = 1
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        }
                    }

                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalysis
                        )
                        cameraControl = camera.cameraControl
                    } catch (e: Exception) {
                        Log.e("Scanner", "Binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(250.dp, 150.dp).border(2.dp, Color.White, RoundedCornerShape(12.dp)))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Enfoca el código de barras", color = Color.White)
                if (detectedCount > 0) {
                    LinearProgressIndicator(
                        progress = { detectedCount.toFloat() / requiredConfidence },
                        modifier = Modifier.padding(top = 8.dp).width(200.dp),
                        color = Color.Green
                    )
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Zoom", color = Color.White, fontSize = 12.sp)
                Slider(
                    value = zoomLevel,
                    onValueChange = {
                        zoomLevel = it
                        cameraControl?.setLinearZoom(it)
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
            }
            Button(
                onClick = {
                    cameraControl?.enableTorch(false)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun OCRScannerView(
    onNutrimentsDetected: (Nutriments) -> Unit,
    onDismiss: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var currentNutriments by remember { mutableStateOf<Nutriments?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    var zoomLevel by remember { mutableFloatStateOf(0f) }
    var torchEnabled by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    var showEditDialog by remember { mutableStateOf<String?>(null) }
    var editValue by remember { mutableStateOf("") }

    // Estados para edición manual completa
    var mostrarEdicionManual by remember { mutableStateOf(false) }
    var manualCal by remember { mutableStateOf("") }
    var manualPro by remember { mutableStateOf("") }
    var manualCarb by remember { mutableStateOf("") }
    var manualFat by remember { mutableStateOf("") }

    fun processCapturedImage(proxy: ImageProxy) {
        val mediaImage = proxy.image ?: run {
            proxy.close()
            isProcessing = false
            return
        }
        val image = InputImage.fromMediaImage(mediaImage, proxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val detected = parseNutritionTextGeometric(visionText, image.width, image.height)
                if (detected != null) {
                    currentNutriments = detected
                }
            }
            .addOnCompleteListener {
                proxy.close()
                isProcessing = false
            }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            try {
                cameraControl?.enableTorch(false)
            } catch (e: Exception) {
                Log.e("OCR", "Error turning off torch", e)
            }
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewViewRef = this
                }
            },
            update = { previewView ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(previewView.context)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }

                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setTargetResolution(android.util.Size(1920, 1080)) // Full HD para máxima nitidez
                        .build()
                    imageCapture = capture

                    try {
                        cameraProvider.unbindAll()
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            capture
                        )
                        cameraControl = camera.cameraControl
                        cameraControl?.enableTorch(torchEnabled)
                    } catch (e: Exception) { Log.e("OCR", "Binding failed", e) }
                }, ContextCompat.getMainExecutor(previewView.context))
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val factory = previewViewRef?.let { SurfaceOrientedMeteringPointFactory(it.width.toFloat(), it.height.toFloat()) }
                        val point = factory?.createPoint(offset.x, offset.y)
                        if (point != null) {
                            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                                .setAutoCancelDuration(3, TimeUnit.SECONDS)
                                .build()
                            cameraControl?.startFocusAndMetering(action)
                        }
                    }
                }
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(320.dp, 420.dp).border(2.dp, Color.Cyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)))
        }

        IconButton(
            onClick = {
                torchEnabled = !torchEnabled
                cameraControl?.enableTorch(torchEnabled)
            },
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                if (torchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Linterna",
                tint = if (torchEnabled) Color.Yellow else Color.White
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth().padding(top = 60.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Analizar Alimento", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Toma una foto a la tabla nutricional", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    NutrimentStatusRow("Calorías", currentNutriments?.calorias) {
                        editValue = (currentNutriments?.calorias ?: 0.0).toString()
                        showEditDialog = "Calorías"
                    }
                    NutrimentStatusRow("Proteínas", currentNutriments?.proteinas) {
                        editValue = (currentNutriments?.proteinas ?: 0.0).toString()
                        showEditDialog = "Proteínas"
                    }
                    NutrimentStatusRow("Carbs", currentNutriments?.carbohidratos) {
                        editValue = (currentNutriments?.carbohidratos ?: 0.0).toString()
                        showEditDialog = "Carbs"
                    }
                    NutrimentStatusRow("Grasas", currentNutriments?.grasas) {
                        editValue = (currentNutriments?.grasas ?: 0.0).toString()
                        showEditDialog = "Grasas"
                    }
                    if (isProcessing) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), color = Color.Cyan)
                        Text("Analizando imagen...", color = Color.Cyan, fontSize = 10.sp)
                    }
                }
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                Text("Zoom", color = Color.White, fontSize = 12.sp)
                Slider(
                    value = zoomLevel,
                    onValueChange = {
                        zoomLevel = it
                        cameraControl?.setLinearZoom(it)
                    },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
            }

            // Botón de Edición Manual
            OutlinedButton(
                onClick = {
                    manualCal = (currentNutriments?.calorias ?: 0.0).toString()
                    manualPro = (currentNutriments?.proteinas ?: 0.0).toString()
                    manualCarb = (currentNutriments?.carbohidratos ?: 0.0).toString()
                    manualFat = (currentNutriments?.grasas ?: 0.0).toString()
                    mostrarEdicionManual = true
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Cyan),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Editar Macros Manualmente")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        cameraControl?.enableTorch(false)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Cancelar")
                }

                IconButton(
                    onClick = {
                        if (!isProcessing) {
                            isProcessing = true
                            imageCapture?.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    processCapturedImage(image)
                                }
                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("OCR", "Capture failed", exception)
                                    isProcessing = false
                                }
                            })
                        }
                    },
                    modifier = Modifier.size(64.dp).background(if (isProcessing) Color.Gray else Color.White, CircleShape)
                ) {
                    if (isProcessing) CircularProgressIndicator(color = Color.Black)
                    else Icon(Icons.Default.CameraAlt, contentDescription = "Capturar", tint = Color.Black)
                }

                Button(
                    onClick = {
                        cameraControl?.enableTorch(false)
                        currentNutriments?.let { onNutrimentsDetected(it) }
                    },
                    enabled = currentNutriments != null && !isProcessing,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text("Confirmar")
                }
            }
        }
    }

    if (mostrarEdicionManual) {
        AlertDialog(
            onDismissRequest = { mostrarEdicionManual = false },
            title = { Text("Editar Valores Nutricionales") },
            text = {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ManualEditBox("Calorías", manualCal, Modifier.weight(1f)) { manualCal = it }
                        ManualEditBox("Proteínas", manualPro, Modifier.weight(1f)) { manualPro = it }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ManualEditBox("Carbs", manualCarb, Modifier.weight(1f)) { manualCarb = it }
                        ManualEditBox("Grasas", manualFat, Modifier.weight(1f)) { manualFat = it }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    currentNutriments = Nutriments(
                        calorias = manualCal.toDoubleOrNull() ?: 0.0,
                        carbohidratos = manualCarb.toDoubleOrNull() ?: 0.0,
                        proteinas = manualPro.toDoubleOrNull() ?: 0.0,
                        grasas = manualFat.toDoubleOrNull() ?: 0.0
                    )
                    mostrarEdicionManual = false
                }) { Text("Aplicar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEdicionManual = false }) { Text("Cancelar") }
            }
        )
    }

    if (showEditDialog != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Corregir $showEditDialog") },
            text = {
                OutlinedTextField(
                    value = editValue,
                    onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) editValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text("Valor real") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val newVal = editValue.toDoubleOrNull() ?: 0.0
                    val old = currentNutriments ?: Nutriments(0.0, 0.0, 0.0, 0.0)
                    currentNutriments = when (showEditDialog) {
                        "Calorías" -> old.copy(calorias = newVal)
                        "Proteínas" -> old.copy(proteinas = newVal)
                        "Carbs" -> old.copy(carbohidratos = newVal)
                        "Grasas" -> old.copy(grasas = newVal)
                        else -> old
                    }
                    showEditDialog = null
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun ManualEditBox(label: String, value: String, modifier: Modifier, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) onValueChange(it) },
        label = { Text(label, fontSize = 10.sp) },
        modifier = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true
    )
}

@Composable
fun NutrimentStatusRow(label: String, value: Double?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Text(
            if (value != null) String.format(Locale.US, "%.1f", value) else "Buscando...",
            color = if (value != null) Color.Green else Color.Yellow,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

fun isSimilar(a: Nutriments, b: Nutriments?): Boolean {
    if (b == null) return false
    val t = 1.0
    return Math.abs((a.calorias ?: 0.0) - (b.calorias ?: 0.0)) < 5.0 &&
            Math.abs((a.proteinas ?: 0.0) - (b.proteinas ?: 0.0)) < t &&
            Math.abs((a.carbohidratos ?: 0.0) - (b.carbohidratos ?: 0.0)) < t &&
            Math.abs((a.grasas ?: 0.0) - (b.grasas ?: 0.0)) < t
}

fun parseNutritionTextGeometric(visionText: Text, imgW: Int, imgH: Int): Nutriments? {
    val allLines = visionText.textBlocks.flatMap { it.lines }.filter { line ->
        val box = line.boundingBox ?: return@filter false
        val cx = box.centerX()
        val cy = box.centerY()
        cx > imgW * 0.1 && cx < imgW * 0.9 &&
                cy > imgH * 0.1 && cy < imgH * 0.9
    }

    var calories: Double? = null
    var proteins: Double? = null
    var carbs: Double? = null
    var fats: Double? = null

    val calKeys = listOf("kcal", "calor", "energy", "energ", "valor")
    val proKeys = listOf("prot", "protein", "proteina")
    val carbKeys = listOf("carbo", "hidratos", "carb", "h. de c. disp", "h. de c", "hidratos de carbono")
    val fatKeys = listOf("grasa", "fat", "lipid", "lipido")

    fun findValueHorizontally(keys: List<String>): Double? {
        val labelLine = allLines.find { line ->
            val text = line.text.lowercase()
            keys.any { text.contains(it) }
        } ?: return null

        val labelBox = labelLine.boundingBox ?: return null
        val centerY = labelBox.centerY()

        val textAfter = labelLine.text.lowercase().substringAfter(keys.find { labelLine.text.lowercase().contains(it) }!!)
        var result = extractNumber(textAfter)

        if (result == null || result == 100.0) {
            result = allLines
                .filter { it != labelLine }
                .filter { line ->
                    val box = line.boundingBox ?: return@filter false
                    Math.abs(box.centerY() - centerY) < (labelBox.height() * 0.8) &&
                            box.left > labelBox.left
                }
                .mapNotNull { extractNumber(it.text) }
                .find { it != 100.0 }
        }

        if (result != null && result > 100.0 && !keys.intersect(calKeys.toSet()).isNotEmpty()) {
            result /= 10.0
        }

        return result
    }

    calories = findValueHorizontally(calKeys)
    proteins = findValueHorizontally(proKeys)
    carbs = findValueHorizontally(carbKeys)
    fats = findValueHorizontally(fatKeys)

    if (calories != null || proteins != null || carbs != null || fats != null) {
        return Nutriments(calories, carbs, proteins, fats)
    }
    return null
}

fun extractNumber(text: String): Double? {
    if (text.isBlank()) return null
    var normalized = text.lowercase()
        .replace("g", "")
        .replace("mg", "")
        .replace("ml", "") // Añadido soporte para ml
        .replace("kcal", "")
        .trim()

    // Tratamos cualquier carácter no numérico entre dígitos como un punto decimal
    normalized = normalized.replace("""(\d+)[^0-9]+(\d+)""".toRegex(), "$1.$2")

    // Caso de espacio simple
    normalized = normalized.replace("""(\d+)\s+(\d+)""".toRegex(), "$1.$2")

    val regex = """\d+(\.\d+)?""".toRegex()
    val match = regex.find(normalized) ?: return null

    return match.value.toDoubleOrNull()
}
