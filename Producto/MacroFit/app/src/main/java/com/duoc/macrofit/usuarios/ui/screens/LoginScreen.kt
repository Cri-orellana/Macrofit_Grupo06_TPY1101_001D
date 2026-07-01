package com.duoc.macrofit.usuarios.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.macrofit.usuarios.utils.SessionManager
import com.duoc.macrofit.usuarios.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegistro: () -> Unit
) {
    // Resetear ViewModel al entrar si no hay sesión activa (post-logout)
    LaunchedEffect(Unit) {
        if (SessionManager.usuarioActual == null) {
            viewModel.resetState()
        }
    }

    // Escuchar cambios en usuarioLogueado para navegar al éxito
    LaunchedEffect(viewModel.usuarioLogueado) {
        if (viewModel.usuarioLogueado != null) {
            onLoginSuccess()
        }
    }

    MacroFitFondoUniversal {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MacroFitHeaderLogo()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bienvenido a Macrofit",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = viewModel.correo,
                onValueChange = { viewModel.correo = it },
                label = { Text("Correo Electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.contrasena,
                onValueChange = { viewModel.contrasena = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de Error (solo aparece si hay un error en el ViewModel)
            viewModel.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }



            if (viewModel.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("Ingresar")
                }
            }
        }
    }
}