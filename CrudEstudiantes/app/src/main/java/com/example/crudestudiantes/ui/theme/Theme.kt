package com.example.crudestudiantes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Colores = lightColorScheme(
    primary = Indigo,
    secondary = Esmeralda,
    background = Fondo,
    surface = Color.White,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,
    error = Rojo
)

@Composable
fun CrudTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Colores,
        typography = Typography,
        content = content
    )
}
