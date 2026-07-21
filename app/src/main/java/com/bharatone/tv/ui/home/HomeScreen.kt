package com.bharatone.tv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.bharatone.tv.data.Channel

private val CATEGORY_ORDER = listOf("News", "National", "Sansad", "Regional", "Test")

@Composable
fun HomeScreen(
    channels: List<Channel>,
    onChannelClick: (Channel) -> Unit,
) {
    val rows = channels
        .groupBy { it.category.ifBlank { "Channels" } }
        .toList()
        .sortedBy { (category, _) -> CATEGORY_ORDER.indexOf(category).takeIf { it >= 0 } ?: Int.MAX_VALUE }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            item { Header() }

            rows.forEach { (category, rowChannels) ->
                item {
                    Text(
                        text = category,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                item { ChannelRow(rowChannels, onChannelClick) }
            }
        }
    }
}

@Composable
private fun Header() {
    Column {
        Text(
            text = "Bharat One",
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "Free live TV · Doordarshan & Sansad TV",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun ChannelRow(channels: List<Channel>, onChannelClick: (Channel) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(channels, key = { it.id }) { channel ->
            ChannelCard(channel) { onChannelClick(channel) }
        }
    }
}

@Composable
private fun ChannelCard(channel: Channel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(width = 260.dp, height = 150.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Text(
                text = channel.name,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (channel.isPlayable) channel.language else "Coming soon",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
