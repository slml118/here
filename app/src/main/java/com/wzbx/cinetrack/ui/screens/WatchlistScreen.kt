package com.wzbx.cinetrack.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wzbx.cinetrack.ui.components.ScreenHeader
import com.wzbx.cinetrack.ui.components.StateContainer
import com.wzbx.cinetrack.ui.components.UiState

/**
 * 我的看单。
 *
 * 阶段 B 之前这里没有数据，正好用 [UiState.Empty] 展示“空态”应该长什么样：
 * 一句说明 + 一个明确的下一步动作，而不是一片白。
 */
@Composable
fun WatchlistScreen(
    onGoDiscover: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            state = UiState.Empty,
            modifier = Modifier.weight(1f),
            emptyText = "看单还是空的。去发现页挑一部，之后这里会按「最该看」自动排序。"
        ) { }
        Button(
            onClick = onGoDiscover,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("去发现页看看")
        }
    }
}
