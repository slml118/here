package com.wzbx.cinetrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wzbx.cinetrack.data.local.WatchItem
import com.wzbx.cinetrack.data.local.WatchStatus
import com.wzbx.cinetrack.ui.components.ScreenHeader
import com.wzbx.cinetrack.ui.components.StateContainer
import com.wzbx.cinetrack.ui.components.UiState
import com.wzbx.cinetrack.ui.watchlist.WatchlistViewModel

/**
 * 我的看单：读数据库，支持推进状态和移除。
 *
 * 空态时底部给一个明确的下一步按钮，而不是让用户对着空白页发呆。
 */
@Composable
fun WatchlistScreen(
    onGoDiscover: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WatchlistViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ScreenHeader(
            title = "我的看单",
            subtitle = "想看 / 在看 / 看过"
        )
        StateContainer(
            state = state,
            modifier = Modifier.weight(1f),
            emptyText = "看单还是空的。去发现页挑一部，加入后这里会立刻出现。"
        ) { items ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(items, key = { it.entry.id }) { item ->
                    WatchlistRow(
                        item = item,
                        onAdvance = { viewModel.advanceStatus(item) },
                        onRemove = { viewModel.remove(item.movie.id) }
                    )
                }
            }
        }
        if (state is UiState.Empty) {
            androidx.compose.material3.Button(
                onClick = onGoDiscover,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("去发现页看看")
            }
        }
    }
}

@Composable
private fun WatchlistRow(
    item: WatchItem,
    onAdvance: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.movie.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${item.entry.status.label} · ${item.movie.genres}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onAdvance) {
                Text(nextActionLabel(item.entry.status))
            }
            TextButton(onClick = onRemove) {
                Text("移除")
            }
        }
    }
}

/** 按钮文案跟着状态走，比一个「切换状态」更清楚下一步会发生什么。 */
private fun nextActionLabel(status: WatchStatus): String = when (status) {
    WatchStatus.WANT -> "开始看"
    WatchStatus.WATCHING -> "看完了"
    WatchStatus.WATCHED -> "重看"
}
