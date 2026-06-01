package com.duoc.macrofit.usuarios.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.duoc.macrofit.R
import com.duoc.macrofit.R.drawable.fondo_app
import com.duoc.macrofit.R.drawable.logo_app

// EL FONDO UNIVERSAL
@Composable
fun MacroFitFondoUniversal(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = fondo_app),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            // ContentScale.Crop asegura que llene la pantalla sin deformarse
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        content()
    }
}

// EL LOGO CENTRADO (logo_app.png)
@Composable
fun MacroFitHeaderLogo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = logo_app),
            contentDescription = "Logo MacroFit",
            modifier = Modifier
                .width(200.dp)
                .wrapContentHeight(),
            contentScale = ContentScale.Fit
        )
    }
}