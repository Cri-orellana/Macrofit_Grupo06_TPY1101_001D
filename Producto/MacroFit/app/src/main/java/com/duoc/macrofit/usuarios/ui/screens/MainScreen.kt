package com.duoc.macrofit.usuarios.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.macrofit.macros.view.DiarioScreen
import com.duoc.macrofit.macros.view.ProgresoScreen
import com.duoc.macrofit.macros.viewmodel.SeleccionarComidaViewModel
import com.duoc.macrofit.nutricion.view.NutricionScreen
import com.duoc.macrofit.rutinas.view.CrearRutinaScreen
import com.duoc.macrofit.rutinas.view.EstadisticasScreen
import com.duoc.macrofit.rutinas.view.RutinasScreen

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    var mostrarCrearRutina by remember { mutableStateOf(false) }
    var idRutinaEditar by remember { mutableStateOf<Int?>(null) }

    // ✅ Una sola instancia compartida
    val macrosViewModel: SeleccionarComidaViewModel = viewModel()

    MacroFitFondoUniversal {
        Scaffold(
            backgroundColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate("progreso") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoGraph,
                        contentDescription = "Progreso",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true,
            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    cutoutShape = CircleShape,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    IconButton(onClick = { navController.navigate("nutricion") }) {
                        Icon(Icons.Filled.Restaurant, contentDescription = "Nutrición", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { navController.navigate("macros") }) {
                        Icon(Icons.Filled.DateRange, contentDescription = "macros", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { navController.navigate("rutinas") }) {
                        Icon(Icons.Filled.FitnessCenter, contentDescription = "Entrenamiento", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    IconButton(onClick = { navController.navigate("perfil") }) {
                        Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavHost(
                    navController = navController,
                    startDestination = "perfil"
                ) {
                    composable("nutricion") {
                        NutricionScreen(macrosViewModel = macrosViewModel)
                    }
                    composable("macros") {
                        DiarioScreen(viewModel = macrosViewModel)
                    }
                    composable("progreso") {
                        ProgresoScreen()
                    }
                    composable("rutinas") {
                        if (mostrarCrearRutina) {
                            CrearRutinaScreen(
                                idRutinaEditar = idRutinaEditar,
                                onVolver = {
                                    Log.d("NAV_RUTINA", "Volver desde CrearRutinaScreen")
                                    idRutinaEditar = null
                                    mostrarCrearRutina = false
                                },
                                onRutinaCreada = {
                                    Log.d("NAV_RUTINA", "Rutina creada/editada, volviendo. idRutinaEditar=$idRutinaEditar")
                                    idRutinaEditar = null
                                    mostrarCrearRutina = false
                                }
                            )
                        } else {
                            RutinasScreen(
                                onCrearRutina = {
                                    Log.d("NAV_RUTINA", "Crear rutina nueva")
                                    idRutinaEditar = null
                                    mostrarCrearRutina = true
                                },
                                onEditarRutina = { rutina ->
                                    Log.d("NAV_RUTINA", "Editar rutina recibida: idRutina=${rutina.idRutina}")
                                    idRutinaEditar = rutina.idRutina
                                    mostrarCrearRutina = true
                                }
                            )
                        }
                    }
                    composable("estadisticas") {
                        EstadisticasScreen()
                    }
                    composable("perfil") {
                        PerfilScreen(onLogout = onLogout)
                    }
                }
            }
        }
    }
}