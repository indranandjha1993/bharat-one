package com.bharatone.tv.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.LocalTextStyle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ProvideTextStyle
import androidx.tv.material3.darkColorScheme

private val BharatOneColors = darkColorScheme(
    primary = BrandColor.SaffronGold,
    onPrimary = BrandColor.Ink,
    background = BrandColor.Ink,
    onBackground = BrandColor.TextHi,
    surface = BrandColor.Surface,
    onSurface = BrandColor.TextHi,
)

@Composable
fun BharatOneTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = BharatOneColors) {
        ProvideTextStyle(LocalTextStyle.current.copy(fontFamily = Poppins), content)
    }
}
