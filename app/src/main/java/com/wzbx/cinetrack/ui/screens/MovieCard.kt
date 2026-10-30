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
import com.wzbx.cinetrack.ui.model.MovieUi

private val posterColors = listOf(
    Color(0xFF2D3A4A),
    Color(0xFF4A2D3A),
    Color(0xFF2D4A3A),
    Color(0xFF3A2D4A)
)

/**
 * 影片卡片。
 *
 * 海报位先用渐变色块占位：阶段 C 会换成 Coil 加载的网络图片，
 * 但卡片结构、文字层级现在就能定下来，后面只换一个图片组件。
 */
@Composable
fun MovieCard(movie: MovieUi, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PosterPlaceholder(movie)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${movie.year} · ${movie.genre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "★ ${movie.rating}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PosterPlaceholder(movie: MovieUi) {
    val base = posterColors[(movie.id % posterColors.size).toInt()]
    Box(
        modifier = Modifier
            .size(width = 64.dp, height = 88.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(listOf(base, Color.Black.copy(alpha = 0.7f)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = movie.title.take(1),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
    }
}
