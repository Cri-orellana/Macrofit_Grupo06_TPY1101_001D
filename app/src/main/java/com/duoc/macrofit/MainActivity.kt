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
import com.duoc.macrofit.ui.screens.LoginScreen
import com.duoc.macrofit.ui.screens.MainScreen
import com.duoc.macrofit.ui.screens.RegistroScreen
import com.duoc.macrofit.ui.theme.MacrofitTheme
import com.duoc.macrofit.utils.SessionManager // El vigilante global

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MacrofitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val usuarioGlobal = SessionManager.usuarioActual

                    var enPantallaRegistro by remember { mutableStateOf(false) }

                    if (usuarioGlobal != null) {
                        MainScreen()
                    } else {
                        if (enPantallaRegistro) {
                            RegistroScreen(
                                onRegistroExitoso = { enPantallaRegistro = false },
                                onVolverAlLogin = { enPantallaRegistro = false }
                            )
                        } else {
                            LoginScreen(
                                onLoginSuccess = { },
                                onNavigateToRegistro = { enPantallaRegistro = true }
                            )
                        }
                    }
                }
            }
        }
    }
}