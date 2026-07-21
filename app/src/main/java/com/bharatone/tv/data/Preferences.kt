package com.bharatone.tv.data

import android.content.Context

class Preferences(context: Context) {

    private val prefs = context.getSharedPreferences("bharat_one", Context.MODE_PRIVATE)

    var lastChannelId: String?
        get() = prefs.getString(KEY_LAST_CHANNEL, null)
        set(value) = prefs.edit().putString(KEY_LAST_CHANNEL, value).apply()

    private companion object {
        const val KEY_LAST_CHANNEL = "last_channel_id"
    }
}
