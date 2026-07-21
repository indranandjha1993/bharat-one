package com.bharatone.tv.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Deep gradient-mesh backdrop: a few soft colour glows over the ink base.
 * Drawn once (no per-frame animation) to stay light on low-end Fire Stick hardware.
 */
@Composable
fun AuroraBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize().background(BrandColor.Ink)) {
        glow(BrandColor.GlowSaffron, Offset(size.width * 0.20f, size.height * 0.14f), size.width * 0.44f, 0.20f)
        glow(BrandColor.GlowMagenta, Offset(size.width * 0.90f, size.height * 0.06f), size.width * 0.40f, 0.18f)
        glow(BrandColor.GlowTeal, Offset(size.width * 0.78f, size.height * 0.98f), size.width * 0.52f, 0.16f)
    }
}

private fun DrawScope.glow(color: Color, center: Offset, radius: Float, alpha: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}
