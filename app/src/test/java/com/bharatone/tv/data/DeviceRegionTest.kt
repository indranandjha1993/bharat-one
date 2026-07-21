package com.bharatone.tv.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeviceRegionTest {

    private val available = setOf("Hindi", "English", "Bengali", "Tamil")

    @Test
    fun `maps a device language code to a channel language we carry`() {
        assertEquals("Bengali", preferredLanguage("bn", available))
        assertEquals("English", preferredLanguage("en", available))
        assertEquals("Tamil", preferredLanguage("ta", available))
    }

    @Test
    fun `returns null when the language is not carried or the code is unknown`() {
        assertNull("Tamil not in catalog", preferredLanguage("ta", setOf("Hindi")))
        assertNull("unknown language code", preferredLanguage("xx", available))
    }
}
