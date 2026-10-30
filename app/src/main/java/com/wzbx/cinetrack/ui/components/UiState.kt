package com.wzbx.cinetrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 页面统一状态。
 *
 * 用 sealed interface 而不是「布尔值 + 错误字符串」，
 * 是因为后者允许出现「既在加载又报错」这种非法组合，编译器也帮不上忙。
 */
sealed interface UiState<out T> {
    /** 首次加载或重试中。 */
    data object Loading : UiState<Nothing>

    /** 请求成功但没有数据。 */
    data object Empty : UiState<Nothing>

    /** 请求失败，[message] 直接给用户看。 */
    data class Error(val message: String) : UiState<Nothing>

    /** 正常数据。 */
    data class Success<T>(val data: T) : UiState<T>
}

/**
 * 把 [UiState] 渲染成四种界面，业务页面只需要提供 [content]。
 *
 * 这样「加载中 / 空 / 错误」就是一套统一实现，不会出现某个页面忘了写错误态。
 */
@Composable
fun <T> StateContainer(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    emptyText: String = "这里还什么都没有",
    onRetry: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            is UiState.Loading -> CircularProgressIndicator()

            is UiState.Empty -> HintText(emptyText)

            is UiState.Error -> ErrorContent(message = state.message, onRetry = onRetry)

            is UiState.Success -> {
                @Suppress("UNCHECKED_CAST")
                content((state as UiState.Success<T>).data)
            }
        }
    }
}

@Composable
private fun HintText(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(24.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}
