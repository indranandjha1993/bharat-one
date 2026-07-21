package com.bharatone.tv.ui.home

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CATEGORY_ORDER = listOf("News", "National", "Sansad", "Learning", "Regional", "Test")
private val TileShape = RoundedCornerShape(16.dp)
private val HeroShape = RoundedCornerShape(22.dp)

@Composable
fun HomeScreen(
    channels: List<Channel>,
    initialFocusId: String? = null,
    onChannelClick: (Channel) -> Unit,
) {
    val browsable = remember(channels) { channels.filter { !it.test } }
    val filterItems = remember(browsable) { buildFilterItems(browsable, CATEGORY_ORDER) }

    var filter by remember { mutableStateOf<Filter>(Filter.All) }
    var focused by remember { mutableStateOf<Channel?>(null) }

    val filtered = remember(browsable, filter) { applyFilter(browsable, filter) }
    val rows = remember(filtered) {
        filtered.groupBy { it.category.ifBlank { "Channels" } }
            .toList()
            .sortedBy { (category, _) -> CATEGORY_ORDER.indexOf(category).takeIf { it >= 0 } ?: Int.MAX_VALUE }
    }

    val heroChannel = focused
        ?: filtered.firstOrNull { it.id == initialFocusId }
        ?: filtered.firstOrNull()

    val focusId = initialFocusId ?: browsable.firstOrNull { it.isPlayable }?.id
    val firstTileFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { firstTileFocus.requestFocus() } }

    Box(Modifier.fillMaxSize()) {
        AuroraBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 44.dp)
                .padding(top = 36.dp),
        ) {
            TopBar(liveCount = browsable.count { it.isPlayable })
            Spacer(Modifier.height(16.dp))
            Hero(heroChannel)
            Spacer(Modifier.height(18.dp))
            FilterRail(items = filterItems, active = filter, onSelect = {
                filter = it
                focused = null
            })
            Spacer(Modifier.height(18.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 40.dp),
            ) {
                rows.forEach { (category, rowChannels) ->
                    item { RowEyebrow(category) }
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                            items(rowChannels, key = { it.id }) { channel ->
                                BroadcastTile(
                                    channel = channel,
                                    modifier = if (channel.id == focusId) Modifier.focusRequester(firstTileFocus) else Modifier,
                                    onFocused = { focused = it },
                                    onClick = { onChannelClick(channel) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(liveCount: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("BHARAT ONE", color = BrandColor.SaffronGold, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        Spacer(Modifier.width(16.dp))
        LivePill(liveCount)
        Spacer(Modifier.weight(1f))
        Clock()
    }
}

@Composable
private fun LivePill(count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(BrandColor.LiveRed.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(BrandColor.LiveRed))
        Spacer(Modifier.width(7.dp))
        Text("$count LIVE", color = BrandColor.TextHi, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun Clock() {
    var time by remember { mutableStateOf(currentTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            time = currentTime()
            delay(10_000)
        }
    }
    Text(time, color = BrandColor.TextMuted, fontSize = 20.sp, fontWeight = FontWeight.Medium)
}

private fun currentTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

@Composable
private fun Hero(channel: Channel?) {
    channel ?: return
    val (native, latin) = categoryEyebrow(channel.category)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(188.dp)
            .clip(HeroShape)
            .background(tileBrush(channel.id))
            .border(1.dp, Color.White.copy(alpha = 0.10f), HeroShape),
    ) {
        Text(
            text = languageGlyph(channel.language),
            color = Color.White.copy(alpha = 0.13f),
            fontSize = 168.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 44.dp),
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.horizontalGradient(0f to Color.Black.copy(alpha = 0.62f), 0.6f to Color.Transparent),
            ),
        )
        Column(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 40.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(9.dp).clip(CircleShape).background(BrandColor.LiveRed))
                Spacer(Modifier.width(8.dp))
                Text("LIVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.width(12.dp))
                Text("$native · $latin", color = BrandColor.SaffronGold, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            }
            Text(channel.name, color = Color.White, fontSize = 46.sp, fontWeight = FontWeight.Black)
            Text("${channel.language} · Public broadcaster", color = BrandColor.TextHi.copy(alpha = 0.85f), fontSize = 17.sp)
            Text("▶  Press OK to watch", color = BrandColor.TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FilterRail(items: List<FilterItem>, active: Filter, onSelect: (Filter) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(items) { item ->
            when (item) {
                is FilterItem.Heading -> Text(
                    text = item.text,
                    color = BrandColor.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
                is FilterItem.Chip -> FilterChip(
                    item = item,
                    selected = item.filter == active,
                    onClick = { onSelect(item.filter) },
                )
            }
        }
    }
}

@Composable
private fun FilterChip(item: FilterItem.Chip, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Surface(
        onClick = onClick,
        shape = ClickableSurfaceDefaults.shape(shape),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (selected) BrandColor.SaffronGold else Color.White.copy(alpha = 0.06f),
            contentColor = if (selected) BrandColor.Ink else BrandColor.TextHi,
            focusedContainerColor = BrandColor.SaffronGold,
            focusedContentColor = BrandColor.Ink,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), shape = shape),
            focusedBorder = Border(BorderStroke(2.dp, BrandColor.SaffronGold), shape = shape),
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (item.glyph != null) {
                Text(item.glyph, fontSize = 14.sp)
                Spacer(Modifier.width(6.dp))
            }
            Text(item.label, fontSize = 14.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
private fun RowEyebrow(category: String) {
    val (native, latin) = categoryEyebrow(category)
    Row(verticalAlignment = Alignment.Bottom) {
        Text(native, color = BrandColor.TextHi, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(10.dp))
        Text(latin, color = BrandColor.SaffronGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp, modifier = Modifier.padding(bottom = 3.dp))
    }
}

@Composable
private fun BroadcastTile(
    channel: Channel,
    modifier: Modifier = Modifier,
    onFocused: (Channel) -> Unit,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .size(width = 244.dp, height = 138.dp)
            .onFocusChanged { if (it.isFocused) onFocused(channel) },
        shape = CardDefaults.shape(TileShape),
        scale = CardDefaults.scale(focusedScale = 1.07f),
        colors = CardDefaults.colors(containerColor = Color.Transparent),
        border = CardDefaults.border(
            border = Border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), shape = TileShape),
            focusedBorder = Border(BorderStroke(3.dp, BrandColor.SaffronGold), shape = TileShape),
        ),
        glow = CardDefaults.glow(focusedGlow = Glow(elevationColor = BrandColor.SaffronGold, elevation = 18.dp)),
    ) {
        Box(Modifier.fillMaxSize().clip(TileShape).background(tileBrush(channel.id))) {
            Text(
                text = languageGlyph(channel.language),
                color = Color.White.copy(alpha = 0.12f),
                fontSize = 72.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp),
            )
            if (channel.isPlayable) {
                TileLiveBug(Modifier.align(Alignment.TopStart).padding(12.dp))
            } else {
                Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.4f)))
            }
            Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                Text(channel.name, color = BrandColor.TextHi, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (channel.isPlayable) channel.language else "Coming soon",
                    color = if (channel.isPlayable) BrandColor.TextMuted else BrandColor.SaffronGold,
                    fontSize = 12.sp,
                )
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
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(BrandColor.LiveRed))
        Spacer(Modifier.width(5.dp))
        Text("LIVE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    }
}
