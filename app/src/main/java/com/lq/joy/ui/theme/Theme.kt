package com.lq.joy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    /*primary = Blue100,
    primaryVariant = Blue300,
    secondary = Blue100,
    secondaryVariant = Blue300,
    surface = Color.White,
    background = Blue50,
    onPrimary = DarkBlue900,
    onSecondary = DarkBlue900,
    onBackground = Grey800,
    onSurface = Color.Black,
    onError = Color.Red*/

    primary = Blue100,
    primaryVariant = Blue300,
    secondary = Blue500,
    secondaryVariant = Blue700,
    surface = Blue50,
    background = Color.White,
    onPrimary = DarkBlue900,
    onSecondary = DarkBlue900,
    onBackground = Color.Black,
    onSurface = Grey800,
    onError = Color.Red
)

@Composable
fun JoyTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}