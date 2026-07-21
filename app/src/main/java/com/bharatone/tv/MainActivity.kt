package com.bharatone.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.bharatone.tv.data.Channel
import com.bharatone.tv.data.ChannelRepository
import com.bharatone.tv.data.Preferences
import com.bharatone.tv.ui.home.HomeScreen
import com.bharatone.tv.ui.player.PlayerScreen
import com.bharatone.tv.ui.theme.BharatOneTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channels = ChannelRepository(applicationContext).load()
        val playable = channels.filter { it.isPlayable && !it.test }
        val preferences = Preferences(applicationContext)

        setContent {
            BharatOneTheme {
                var current by remember { mutableStateOf<Channel?>(null) }
                var lastChannelId by remember { mutableStateOf(preferences.lastChannelId) }

                fun open(channel: Channel) {
                    current = channel
                    lastChannelId = channel.id
                    preferences.lastChannelId = channel.id
                }

                when (val channel = current) {
                    null -> HomeScreen(
                        channels = channels,
                        initialFocusId = lastChannelId,
                        onChannelClick = { if (it.isPlayable) open(it) },
                    )
                    else -> {
                        BackHandler { current = null }
                        PlayerScreen(
                            channel = channel,
                            playlist = playable,
                            onSwitch = ::open,
                        )
                    }
                }
            }
        }
    }
}
