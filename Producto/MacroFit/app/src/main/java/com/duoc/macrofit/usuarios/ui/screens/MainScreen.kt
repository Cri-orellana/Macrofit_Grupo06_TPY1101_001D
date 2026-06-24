package com.duoc.macrofit.usuarios.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.duoc.macrofit.macros.viewmodel.SeleccionarComidaViewModel
import com.duoc.macrofit.nutricion.view.NutricionScreen
import com.duoc.macrofit.rutinas.view.CrearRutinaScreen
import com.duoc.macrofit.rutinas.view.RutinasScreen

@Composable
fun MainScreen() {
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
                    onClick = { }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Añadir",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            isFloatingActionButtonDocked = true,
            bottomBar = {
                BottomAppBar(
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
                    // ✅ Solo UNA vez, con el viewModel compartido
                    composable("macros") {
                        DiarioScreen(viewModel = macrosViewModel)
                    }
                    composable("rutinas") {
                        if (mostrarCrearRutina) {
                            CrearRutinaScreen(
                                idRutinaEditar = idRutinaEditar,
                                onVolver = {
                                    idRutinaEditar = null
                                    mostrarCrearRutina = false
                                },
                                onRutinaCreada = {
                                    idRutinaEditar = null
                                    mostrarCrearRutina = false
                                }
                            )
                        } else {
                            RutinasScreen(
                                onCrearRutina = {
                                    idRutinaEditar = null
                                    mostrarCrearRutina = true
                                },
                                onEditarRutina = { rutina ->
                                    idRutinaEditar = rutina.idRutina
                                    mostrarCrearRutina = true
                                }
                            )
                        }
                    }
                    composable("perfil") {
                        PerfilScreen()
                    }
                }
            }
        }
    }
}