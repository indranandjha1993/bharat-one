package com.bharatone.tv.ui.home

import androidx.annotation.OptIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.bharatone.tv.data.Channel
import com.bharatone.tv.ui.theme.AuroraBackground
import com.bharatone.tv.ui.theme.BrandColor
import com.bharatone.tv.ui.theme.categoryEyebrow
import com.bharatone.tv.ui.theme.languageGlyph
import com.bharatone.tv.ui.theme.tileBrush
import kotlinx.coroutines.delay

private val CATEGORY_ORDER = listOf("News", "National", "Sansad", "Learning", "Regional", "Test")
private val PreviewShape = RoundedCornerShape(20.dp)
private val CardShape = RoundedCornerShape(14.dp)

@OptIn(UnstableApi::class)
@Composable
fun LiveGuideScreen(
    channels: List<Channel>,
    initialFocusId: String? = null,
    onChannelClick: (Channel) -> Unit,
) {
    val browsable = remember(channels) { channels.filter { !it.test && it.isPlayable } }
    val items = remember(browsable) { buildFilterItems(browsable, CATEGORY_ORDER) }

    var selected by remember { mutableStateOf<Filter>(Filter.All) }
    var previewChannel by remember {
        mutableStateOf(browsable.firstOrNull { it.id == initialFocusId } ?: browsable.firstOrNull())
    }

    val listChannels = remember(browsable, selected) { applyFilter(browsable, selected) }

    val context = LocalContext.current
    val preview = remember {
        ExoPlayer.Builder(context).build().apply {
            volume = 0f
            playWhenReady = true
        }
    }
    DisposableEffect(Unit) { onDispose { preview.release() } }

    // Debounced: only load the preview once focus settles on a channel for ~450ms.
    LaunchedEffect(previewChannel?.id) {
        val channel = previewChannel ?: return@LaunchedEffect
        delay(450)
        preview.setMediaItem(MediaItem.fromUri(channel.streamUrl))
        preview.prepare()
    }

    Box(Modifier.fillMaxSize()) {
        AuroraBackground()
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 40.dp, top = 34.dp, end = 40.dp, bottom = 28.dp),
        ) {
            CategoryColumn(
                items = items,
                selected = selected,
                modifier = Modifier.width(232.dp).fillMaxHeight(),
                onSelect = { filter ->
                    selected = filter
                    previewChannel = applyFilter(browsable, filter).firstOrNull()
                },
            )
            Spacer(Modifier.width(28.dp))
            Column(Modifier.fillMaxSize()) {
                PreviewPane(previewChannel, preview)
                Spacer(Modifier.height(18.dp))
                ChannelGrid(
                    channels = listChannels,
                    focusId = initialFocusId,
                    onFocused = { previewChannel = it },
                    onClick = onChannelClick,
                )
            }
        }
    }
}

@Composable
private fun CategoryColumn(
    items: List<FilterItem>,
    selected: Filter,
    modifier: Modifier = Modifier,
    onSelect: (Filter) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(items) { item ->
            when (item) {
                is FilterItem.Heading -> Text(
                    text = item.text,
                    color = BrandColor.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 12.dp, bottom = 4.dp),
                )
                is FilterItem.Chip -> CategoryRow(
                    item = item,
                    selected = item.filter == selected,
                    onSelect = { onSelect(item.filter) },
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(item: FilterItem.Chip, selected: Boolean, onSelect: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Surface(
        onClick = onSelect,
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if (it.isFocused) onSelect() },
        shape = ClickableSurfaceDefaults.shape(shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) BrandColor.SaffronGold.copy(alpha = 0.18f) else Color.Transparent,
            contentColor = if (selected) BrandColor.SaffronGold else BrandColor.TextHi,
            focusedContainerColor = BrandColor.SaffronGold,
            focusedContentColor = BrandColor.Ink,
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = Border(BorderStroke(2.dp, BrandColor.SaffronGold), shape = shape),
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (item.glyph != null) {
                Text(item.glyph, fontSize = 15.sp)
                Spacer(Modifier.width(10.dp))
            }
            Text(item.label, fontSize = 15.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
private fun PreviewPane(channel: Channel?, player: ExoPlayer) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(PreviewShape)
            .background(channel?.let { tileBrush(it.id) } ?: Brush.linearGradient(listOf(BrandColor.Surface, BrandColor.Ink)))
            .border(1.dp, Color.White.copy(alpha = 0.10f), PreviewShape),
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    isFocusable = false
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(0.35f to Color.Transparent, 1f to Color.Black.copy(alpha = 0.78f)),
            ),
        )
        if (channel != null) {
            val (native, latin) = categoryEyebrow(channel.category)
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(9.dp).clip(CircleShape).background(BrandColor.LiveRed))
                    Spacer(Modifier.width(8.dp))
                    Text("LIVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.width(12.dp))
                    Text("$native · $latin", color = BrandColor.SaffronGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Text(channel.name, color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Black)
                Text("${channel.language} · Public broadcaster    ▶  OK to watch full screen", color = BrandColor.TextHi.copy(alpha = 0.82f), fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun ChannelGrid(
    channels: List<Channel>,
    focusId: String?,
    onFocused: (Channel) -> Unit,
    onClick: (Channel) -> Unit,
) {
    val firstFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { firstFocus.requestFocus() } }
    val focusTarget = focusId ?: channels.firstOrNull()?.id

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        items(channels, key = { it.id }) { channel ->
            ChannelCard(
                channel = channel,
                modifier = if (channel.id == focusTarget) Modifier.focusRequester(firstFocus) else Modifier,
                onFocused = { onFocused(channel) },
                onClick = { onClick(channel) },
            )
        }
    }
}

@Composable
private fun ChannelCard(
    channel: Channel,
    modifier: Modifier = Modifier,
    onFocused: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
            .onFocusChanged { if (it.isFocused) onFocused() },
        shape = CardDefaults.shape(CardShape),
        scale = CardDefaults.scale(focusedScale = 1.08f),
        colors = CardDefaults.colors(containerColor = Color.Transparent),
        border = CardDefaults.border(
            border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), shape = CardShape),
            focusedBorder = Border(BorderStroke(3.dp, BrandColor.SaffronGold), shape = CardShape),
        ),
        glow = CardDefaults.glow(focusedGlow = Glow(elevationColor = BrandColor.SaffronGold, elevation = 16.dp)),
    ) {
        Box(Modifier.fillMaxSize().clip(CardShape).background(tileBrush(channel.id))) {
            Text(
                text = languageGlyph(channel.language),
                color = Color.White.copy(alpha = 0.12f),
                fontSize = 54.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp),
            )
            TileLiveBug(Modifier.align(Alignment.TopStart).padding(10.dp))
            Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                Text(channel.name, color = BrandColor.TextHi, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(channel.language, color = BrandColor.TextMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun BoxScope.TileLiveBug(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.35f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(5.dp).clip(CircleShape).background(BrandColor.LiveRed))
        Spacer(Modifier.width(4.dp))
        Text("LIVE", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
