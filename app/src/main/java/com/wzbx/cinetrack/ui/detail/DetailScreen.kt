package com.wzbx.cinetrack.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.wzbx.cinetrack.ui.components.StateContainer

/**
 * 影片详情：从列表点进来，展示简介并支持加入看单。
 */
@Composable
fun DetailScreen(
    movieId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel()
) {
    LaunchedEffect(movieId) { viewModel.load(movieId) }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val inWatchlist by viewModel.inWatchlist.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
        ) {
            Text("← 返回")
        }

        StateContainer(
            state = state,
            modifier = Modifier.weight(1f),
            onRetry = { viewModel.load(movieId) }
        ) { detail ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                if (detail.posterUrl != null) {
                    AsyncImage(
                        model = detail.posterUrl,
                        contentDescription = detail.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(Modifier.height(16.dp))
                }

                Text(text = detail.title, style = MaterialTheme.typography.headlineSmall)

                Spacer(Modifier.height(8.dp))
                Text(
                    text = buildString {
                        append(detail.year)
                        append(" · ")
                        append(detail.genres)
                        if (detail.runtimeMinutes > 0) {
                            append(" · ")
                            append(detail.runtimeMinutes)
                            append(" 分钟")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "★ %.1f".format(detail.rating),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(16.dp))
                Text(text = detail.overview, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = viewModel::addToWatchlist,
                    enabled = !inWatchlist,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (inWatchlist) "已在看单" else "加入看单")
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
