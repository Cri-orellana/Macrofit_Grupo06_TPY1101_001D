package com.duoc.macrofit.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = VerdePrincipal,
    background = FondoNegro,
    surface = SuperficieGris,
    onPrimary = TextoOscuro,
    onBackground = TextoClaro,
    onSurface = TextoClaro
)

@Composable
fun MacrofitTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}