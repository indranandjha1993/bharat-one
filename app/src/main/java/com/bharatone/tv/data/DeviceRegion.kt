package com.bharatone.tv.data

private val LANGUAGE_BY_CODE = mapOf(
    "hi" to "Hindi",
    "en" to "English",
    "ur" to "Urdu",
    "bn" to "Bengali",
    "mr" to "Marathi",
    "kn" to "Kannada",
    "te" to "Telugu",
    "ml" to "Malayalam",
    "ta" to "Tamil",
    "as" to "Assamese",
    "gu" to "Gujarati",
    "kok" to "Konkani",
    "ks" to "Kashmiri",
    "mni" to "Manipuri",
    "or" to "Odia",
    "pa" to "Punjabi",
)

/** Maps the viewer's device language code to a channel language we carry, if any. */
fun preferredLanguage(languageCode: String, available: Collection<String>): String? {
    val name = LANGUAGE_BY_CODE[languageCode] ?: return null
    return name.takeIf { it in available }
}
