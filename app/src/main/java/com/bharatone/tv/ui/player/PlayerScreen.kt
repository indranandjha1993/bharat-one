package com.bharatone.tv.ui.player

import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Text
import com.bharatone.tv.data.Channel
import com.bharatone.tv.ui.theme.BrandColor

private enum class PlaybackStatus { Tuning, Playing, OffAir }

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(channel: Channel, playlist: List<Channel>, onSwitch: (Channel) -> Unit) {
    val context = LocalContext.current
    var status by remember(channel.id) { mutableStateOf(PlaybackStatus.Tuning) }

    val player = remember(channel.id) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(channel.streamUrl))
            playWhenReady = true
            prepare()
        }
    }

    DisposableEffect(channel.id) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) status = PlaybackStatus.Playing
            }

            override fun onPlayerError(error: PlaybackException) {
                status = PlaybackStatus.OffAir
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focusRequester.requestFocus() } }
    val index = playlist.indexOfFirst { it.id == channel.id }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown || index < 0 || playlist.isEmpty()) return@onPreviewKeyEvent false
                when (event.key) {
                    Key.DirectionUp -> {
                        onSwitch(playlist[(index - 1 + playlist.size) % playlist.size]); true
                    }
                    Key.DirectionDown -> {
                        onSwitch(playlist[(index + 1) % playlist.size]); true
                    }
                    else -> false
                }
            },
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    keepScreenOn = true
                }
            },
        )

        when (status) {
            PlaybackStatus.Tuning -> CenterMessage("Tuning in to ${channel.name}…")
            PlaybackStatus.OffAir -> CenterMessage("${channel.name} is off air right now.\nPress Back to return.")
            PlaybackStatus.Playing -> ChannelBug(
                name = channel.name,
                modifier = Modifier.align(Alignment.TopStart).padding(28.dp),
            )
        }

        Text(
            text = "▲ ▼  change channel     ◀ Back  all channels",
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 28.dp),
        )
    }
}

@Composable
private fun BoxScope.CenterMessage(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 20.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.align(Alignment.Center).padding(24.dp),
    )
}

@Composable
private fun ChannelBug(name: String, modifier: Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(BrandColor.LiveRed))
        Spacer(Modifier.width(8.dp))
        Text("LIVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.width(10.dp))
        Text("· $name", color = BrandColor.TextHi, fontSize = 14.sp)
    }
}
