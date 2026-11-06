package com.wzbx.cinetrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wzbx.cinetrack.ui.components.ScreenHeader
import com.wzbx.cinetrack.ui.components.StateContainer
import com.wzbx.cinetrack.ui.discover.DiscoverViewModel

/**
 * 发现页：列表来自 Room，加入看单直接写库。
 *
 * 数据流：Room(Flow) → ViewModel(StateFlow) → Compose。
 * 这里没有手动刷新逻辑，数据库变了界面会自己更新。
 */
@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val watchlistIds by viewModel.watchlistIds.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ScreenHeader(
            title = "发现",
            subtitle = "内置片库 · 阶段 C 接入真实接口"
        )
        StateContainer(
            state = state,
            modifier = Modifier.weight(1f),
            emptyText = "片库为空，稍后再试。"
        ) { movies ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(movies, key = { it.id }) { movie ->
                    MovieCard(
                        title = movie.title,
                        meta = "${movie.releaseDate?.take(4) ?: "待定"} · ${movie.genres}",
                        rating = movie.voteAverage,
                        seed = movie.id
                    ) {
                        if (movie.id in watchlistIds) {
                            Text(
                                text = "已加入",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            TextButton(onClick = { viewModel.addToWatchlist(movie.id) }) {
                                Text("加入看单")
                            }
                        }
                    }
                }
            }
        }
    }
}
