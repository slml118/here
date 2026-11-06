package com.wzbx.cinetrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * 发现页：拉取 TMDB 本周热门。
 *
 * 状态机：Loading → Success / Empty / Error(可重试)。
 * 没有本地缓存兜底，网络失败就明确告诉用户。
 */
@Composable
fun DiscoverScreen(
    onMovieClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ScreenHeader(
            title = "发现",
            subtitle = "本周热门 · 数据来自 TMDB"
        )
        StateContainer(
            state = state,
            modifier = Modifier.weight(1f),
            emptyText = "本周没有热门影片",
            onRetry = viewModel::load
        ) { movies ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(movies, key = { it.id }) { movie ->
                    MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
                }
            }
        }
    }
}
