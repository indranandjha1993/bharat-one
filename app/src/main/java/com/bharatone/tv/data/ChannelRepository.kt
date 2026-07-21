package com.bharatone.tv.data

import android.content.Context
import kotlinx.serialization.json.Json

class ChannelRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    fun load(): List<Channel> {
        val raw = context.assets.open(MANIFEST).bufferedReader().use { it.readText() }
        return json.decodeFromString<List<Channel>>(raw)
    }

    private companion object {
        const val MANIFEST = "channels.json"
    }
}
