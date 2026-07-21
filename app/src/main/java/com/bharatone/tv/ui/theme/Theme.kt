package com.bharatone.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

private val BharatOneColors = darkColorScheme(
    primary = Color(0xFFFF6A2C),
    onPrimary = Color(0xFF1A0A00),
    background = Color(0xFF0B1020),
    onBackground = Color(0xFFEDEFF7),
    surface = Color(0xFF141A2E),
    onSurface = Color(0xFFEDEFF7),
)

@Composable
fun BharatOneTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = BharatOneColors, content = content)
}
