package com.bharatone.tv.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Glow
import androidx.tv.material3.Text
import com.bharatone.tv.data.Channel
import com.bharatone.tv.ui.theme.BrandColor
import com.bharatone.tv.ui.theme.categoryEyebrow
import com.bharatone.tv.ui.theme.languageGlyph
import com.bharatone.tv.ui.theme.tileBrush

private val CATEGORY_ORDER = listOf("News", "National", "Sansad", "Learning", "Regional", "Test")
private val TileShape = RoundedCornerShape(16.dp)

@Composable
fun HomeScreen(
    channels: List<Channel>,
    initialFocusId: String? = null,
    onChannelClick: (Channel) -> Unit,
) {
    val rows = channels
        .groupBy { it.category.ifBlank { "Channels" } }
        .toList()
        .sortedBy { (category, _) -> CATEGORY_ORDER.indexOf(category).takeIf { it >= 0 } ?: Int.MAX_VALUE }

    val focusId = initialFocusId ?: channels.firstOrNull { it.isPlayable }?.id
    val focusTile = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focusTile.requestFocus() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandColor.Ink)
            .padding(horizontal = 48.dp)
            .padding(top = 48.dp),
    ) {
        TopBar(liveCount = channels.count { it.isPlayable && !it.test })
        Spacer(Modifier.height(28.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(30.dp),
            contentPadding = PaddingValues(bottom = 48.dp),
        ) {
            rows.forEach { (category, rowChannels) ->
                item { RowEyebrow(category) }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        items(rowChannels, key = { it.id }) { channel ->
                            BroadcastTile(
                                channel = channel,
                                modifier = if (channel.id == focusId) Modifier.focusRequester(focusTile) else Modifier,
                                onClick = { onChannelClick(channel) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopBar(liveCount: Int) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "BHARAT ONE",
                color = BrandColor.SaffronGold,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.width(20.dp))
            LivePill(liveCount)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Free public-broadcaster television",
            color = BrandColor.TextMuted,
            fontSize = 16.sp,
        )
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .width(180.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.horizontalGradient(listOf(BrandColor.Saffron, Color.White, BrandColor.IndiaGreen))),
        )
    }
}

@Composable
private fun LivePill(count: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(BrandColor.LiveRed.copy(alpha = 0.16f))
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(BrandColor.LiveRed))
        Spacer(Modifier.width(8.dp))
        Text("$count LIVE", color = BrandColor.TextHi, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun RowEyebrow(category: String) {
    val (native, latin) = categoryEyebrow(category)
    Row(verticalAlignment = Alignment.Bottom) {
        Text(native, color = BrandColor.TextHi, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(10.dp))
        Text(
            text = latin,
            color = BrandColor.SaffronGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp,
            modifier = Modifier.padding(bottom = 3.dp),
        )
    }
}

@Composable
private fun BroadcastTile(channel: Channel, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.size(width = 268.dp, height = 158.dp),
        shape = CardDefaults.shape(TileShape),
        scale = CardDefaults.scale(focusedScale = 1.06f),
        colors = CardDefaults.colors(containerColor = Color.Transparent),
        border = CardDefaults.border(
            focusedBorder = Border(BorderStroke(3.dp, BrandColor.SaffronGold), shape = TileShape),
        ),
        glow = CardDefaults.glow(
            focusedGlow = Glow(elevationColor = BrandColor.SaffronGold, elevation = 16.dp),
        ),
    ) {
        Box(Modifier.fillMaxSize().clip(TileShape).background(tileBrush(channel.id))) {
            Text(
                text = languageGlyph(channel.language),
                color = Color.White.copy(alpha = 0.12f),
                fontSize = 84.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 14.dp),
            )

            if (channel.isPlayable) {
                TileLiveBug(Modifier.align(Alignment.TopStart).padding(14.dp))
            } else {
                Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.4f)))
            }

            Column(Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                Text(channel.name, color = BrandColor.TextHi, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (channel.isPlayable) "${channel.language} · ${channel.category}" else "Coming soon",
                    color = if (channel.isPlayable) BrandColor.TextMuted else BrandColor.SaffronGold,
                    fontSize = 13.sp,
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
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(BrandColor.LiveRed))
        Spacer(Modifier.width(6.dp))
        Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
    }
}
