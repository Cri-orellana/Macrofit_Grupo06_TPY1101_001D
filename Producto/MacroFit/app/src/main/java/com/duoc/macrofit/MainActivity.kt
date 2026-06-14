package com.duoc.macrofit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.duoc.macrofit.usuarios.ui.screens.LoginScreen
import com.duoc.macrofit.usuarios.ui.screens.MainScreen
import com.duoc.macrofit.usuarios.ui.screens.RegistroScreen
import com.duoc.macrofit.usuarios.ui.theme.MacrofitTheme
import com.duoc.macrofit.usuarios.utils.SessionManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SessionManager.init(applicationContext)

        setContent {
            MacrofitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLogged by remember { mutableStateOf(SessionManager.usuarioActual != null) }
                    var enPantallaRegistro by remember { mutableStateOf(false) }

                    if (isLogged) {
                        MainScreen()
                    } else {
                        if (enPantallaRegistro) {
                            RegistroScreen(
                                onRegistroExitoso = {
                                    if(SessionManager.usuarioActual != null) {
                                        isLogged = true
                                    }
                                    enPantallaRegistro = false
                                },
                                onVolverAlLogin = { enPantallaRegistro = false }
                            )
                        } else {
                            LoginScreen(
                                onLoginSuccess = {
                                    isLogged = true
                                },
                                onNavigateToRegistro = { enPantallaRegistro = true }
                            )
                        }
                    }
                }
            }
        }
    }
}