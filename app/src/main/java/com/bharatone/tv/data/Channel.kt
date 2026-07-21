package com.bharatone.tv.data

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: String,
    val name: String,
    val language: String = "",
    val category: String = "",
    val streamUrl: String = "",
    val logoUrl: String = "",
    val source: String = "",
    val verified: Boolean = false,
    val test: Boolean = false,
) {
    val isPlayable: Boolean get() = streamUrl.isNotBlank()
}
