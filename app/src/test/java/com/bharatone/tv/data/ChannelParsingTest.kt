package com.bharatone.tv.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChannelParsingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `parses channels and derives playable flag from stream url`() {
        val raw = """
            [
              {"id":"dd-news","name":"DD News","language":"Hindi","category":"News","streamUrl":"https://x/y.m3u8"},
              {"id":"dd-bharati","name":"DD Bharati","category":"National","streamUrl":""}
            ]
        """.trimIndent()

        val channels = json.decodeFromString<List<Channel>>(raw)

        assertEquals(2, channels.size)
        assertEquals("DD News", channels[0].name)
        assertTrue("a channel with a stream url is playable", channels[0].isPlayable)
        assertFalse("a channel with a blank stream url is not playable", channels[1].isPlayable)
    }

    @Test
    fun `unknown fields do not break parsing`() {
        val raw = """[{"id":"x","name":"X","streamUrl":"https://x/y.m3u8","futureField":true}]"""
        val channels = json.decodeFromString<List<Channel>>(raw)
        assertEquals(1, channels.size)
        assertTrue(channels[0].isPlayable)
    }
}
