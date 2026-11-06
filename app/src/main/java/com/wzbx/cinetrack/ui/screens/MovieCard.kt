package com.wzbx.cinetrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private val posterColors = listOf(
    Color(0xFF2D3A4A),
    Color(0xFF4A2D3A),
    Color(0xFF2D4A3A),
    Color(0xFF3A2D4A)
)

/**
 * 影片卡片。
 *
 * 海报位先用渐变色块占位，阶段 C 换成 Coil 加载的网络图片时只改这里。
 * [trailing] 用来放「加入看单」之类的操作，发现页和看单页复用同一个卡片。
 */
@Composable
fun MovieCard(
    title: String,
    meta: String,
    rating: Double,
    seed: Long,
    modifier: Modifier = Modifier,
    trailing: @Composable () -> Unit = {}
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterPlaceholder(title = title, seed = seed)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "★ $rating",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            trailing()
        }
    }
}

@Composable
private fun PosterPlaceholder(title: String, seed: Long) {
    val base = posterColors[((seed % posterColors.size).toInt() + posterColors.size) % posterColors.size]
    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(base, Color.Black.copy(alpha = 0.7f)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title.take(1),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}
