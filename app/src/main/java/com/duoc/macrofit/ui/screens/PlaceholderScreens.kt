package com.duoc.macrofit.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NutricionScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("🍏 Módulo de Nutrición", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun DiarioScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("📅 Diario y Progreso", color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun RutinasScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("💪 Rutinas PPL", color = MaterialTheme.colorScheme.onBackground)
    }
}

