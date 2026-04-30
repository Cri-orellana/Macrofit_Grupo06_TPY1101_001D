
package com.duoc.macrofit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = { /* Aquí abriremos el menú para añadir comida o peso */ }
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
        backgroundColor = MaterialTheme.colorScheme.background,

        bottomBar = {
            BottomAppBar(
                cutoutShape = CircleShape,
                backgroundColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                IconButton(onClick = { navController.navigate("nutricion") }) {
                    Icon(Icons.Filled.Restaurant, contentDescription = "Nutrición", tint = MaterialTheme.colorScheme.onBackground)
                }

                IconButton(onClick = { navController.navigate("diario") }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Diario", tint = MaterialTheme.colorScheme.onBackground)
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
                startDestination = "rutinas"
            ) {
                composable("nutricion") { NutricionScreen() }
                composable("diario") { DiarioScreen() }
                composable("rutinas") { RutinasScreen() }

                composable("perfil") {
                    PerfilScreen()
                }
            }
        }
    }
}