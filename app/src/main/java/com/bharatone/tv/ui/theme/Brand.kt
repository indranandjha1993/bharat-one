package com.bharatone.tv.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

object BrandColor {
    val Ink = Color(0xFF141019)
    val InkSoft = Color(0xFF1E1826)
    val Surface = Color(0xFF241D2E)
    val SaffronGold = Color(0xFFF4A81D)
    val LiveRed = Color(0xFFE5342B)
    val TextHi = Color(0xFFF7F5F2)
    val TextMuted = Color(0xFFA79FB0)
    val Saffron = Color(0xFFFF9933)
    val IndiaGreen = Color(0xFF138808)
}

private val tileGradients = listOf(
    listOf(Color(0xFF3A1C71), Color(0xFF1B1533)),
    listOf(Color(0xFF134E5E), Color(0xFF0B2027)),
    listOf(Color(0xFF6A2C34), Color(0xFF2A0E14)),
    listOf(Color(0xFF1D4E3B), Color(0xFF0C231B)),
    listOf(Color(0xFF2C3E50), Color(0xFF161F2A)),
    listOf(Color(0xFF5B2A54), Color(0xFF241026)),
)

fun tileBrush(id: String): Brush =
    Brush.linearGradient(tileGradients[abs(id.hashCode()) % tileGradients.size])

/** Each channel wears its language's own script as a watermark — the point of a public broadcaster. */
fun languageGlyph(language: String): String = when (language) {
    "Hindi" -> "हिं"
    "English" -> "Aa"
    "Bengali" -> "বাং"
    "Marathi" -> "म"
    "Kannada" -> "ಕ"
    "Telugu" -> "తె"
    "Malayalam" -> "മ"
    "Tamil" -> "த"
    "Urdu" -> "اُ"
    else -> "●"
}

fun categoryEyebrow(category: String): Pair<String, String> = when (category) {
    "News" -> "समाचार" to "NEWS"
    "National" -> "राष्ट्रीय" to "NATIONAL"
    "Sansad" -> "संसद" to "PARLIAMENT"
    "Regional" -> "क्षेत्रीय" to "REGIONAL"
    "Test" -> "परीक्षण" to "TEST"
    else -> category to category.uppercase()
}
