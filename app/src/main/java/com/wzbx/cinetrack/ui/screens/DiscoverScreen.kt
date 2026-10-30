package com.wzbx.cinetrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wzbx.cinetrack.ui.components.ScreenHeader
import com.wzbx.cinetrack.ui.components.StateContainer
import com.wzbx.cinetrack.ui.components.UiState
import com.wzbx.cinetrack.ui.model.SampleMovies

/**
 * 发现页：列表结构的样板。
 *
 * 现在喂的是 [SampleMovies]，阶段 C 换成 ViewModel 里的真实网络数据，
 * 页面代码基本不动。
 */
@Composable
fun DiscoverScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ScreenHeader(
            title = "发现",
            subtitle = "示例数据 · 阶段 C 接入真实接口"
        )
        StateContainer(
            state = UiState.Success(SampleMovies.list),
            modifier = Modifier.weight(1f)
        ) { movies ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(movies) { movie ->
                    MovieCard(movie)
                }
            }
        }
    }
}
