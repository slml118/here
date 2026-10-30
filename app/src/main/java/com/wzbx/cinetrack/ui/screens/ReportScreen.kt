package com.wzbx.cinetrack.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wzbx.cinetrack.ui.components.ScreenHeader
import com.wzbx.cinetrack.ui.components.StateContainer
import com.wzbx.cinetrack.ui.components.UiState

/**
 * 年度报告。
 *
 * 阶段 D 之前没有统计数据，同样先给出空态文案，
 * 避免用户点进来看到一片空白以为 App 坏了。
 */
@Composable
fun ReportScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ScreenHeader(
            title = "年度报告",
            subtitle = "看完的片子会自动汇总"
        )
        StateContainer(
            state = UiState.Empty,
            modifier = Modifier.weight(1f),
            emptyText = "还没有 2026 年的观影记录。等你看完第一部，这里会生成统计和洞察。"
        ) { }
    }
}
