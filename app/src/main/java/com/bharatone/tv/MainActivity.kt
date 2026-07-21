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
import com.bharatone.tv.ui.home.HomeScreen
import com.bharatone.tv.ui.player.PlayerScreen
import com.bharatone.tv.ui.theme.BharatOneTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channels = ChannelRepository(applicationContext).load()
        val playable = channels.filter { it.isPlayable }

        setContent {
            BharatOneTheme {
                var current by remember { mutableStateOf<Channel?>(null) }

                when (val channel = current) {
                    null -> HomeScreen(
                        channels = channels,
                        onChannelClick = { if (it.isPlayable) current = it },
                    )
                    else -> {
                        BackHandler { current = null }
                        PlayerScreen(
                            channel = channel,
                            playlist = playable,
                            onSwitch = { current = it },
                        )
                    }
                }
            }
        }
    }
}
