package com.wzbx.cinetrack.ui.discover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wzbx.cinetrack.data.MovieRepository
import com.wzbx.cinetrack.ui.components.UiState
import com.wzbx.cinetrack.ui.model.MovieUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * 发现页：分页加载「本周热门」。
 *
 * 第一页走完整状态机（加载/空/错误），后续页只做追加——
 * 加载更多失败时保留已经看到的内容，不把整页打回错误态，
 * 否则用户滑到第 3 页网络抖一下，前面 40 部全没了。
 */
class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository()

    private val _state = MutableStateFlow<UiState<List<MovieUi>>>(UiState.Loading)
    val state: StateFlow<UiState<List<MovieUi>>> = _state.asStateFlow()

    private val _loadingMore = MutableStateFlow(false)
    val loadingMore: StateFlow<Boolean> = _loadingMore.asStateFlow()

    private var page = 0
    private var endReached = false

    init {
        refresh()
    }

    /** 首次进入或点「重试」。会清空列表重新从第 1 页开始。 */
    fun refresh() {
        page = 0
        endReached = false
        _state.value = UiState.Loading

        viewModelScope.launch {
            runCatching { repository.trending(page = 1) }
                .onSuccess { result ->
                    page = result.page
                    endReached = result.movies.isEmpty() || result.page >= result.totalPages
                    _state.value = if (result.movies.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(result.movies)
                    }
                }
                .onFailure { error ->
                    _state.value = UiState.Error(error.toFriendlyMessage())
                }
        }
    }

    /** 滑到接近底部时调用。重复调用是安全的，内部会拦住。 */
    fun loadMore() {
        val current = (_state.value as? UiState.Success)?.data ?: return
        if (_loadingMore.value || endReached) return

        _loadingMore.value = true
        viewModelScope.launch {
            runCatching { repository.trending(page = page + 1) }
                .onSuccess { result ->
                    page = result.page
                    endReached = result.movies.isEmpty() || result.page >= result.totalPages

                    if (result.movies.isNotEmpty()) {
                        // 去重：TMDB 分页偶尔会在相邻页重复同一条。
                        val existingIds = current.mapTo(HashSet()) { it.id }
                        val appended = result.movies.filterNot { it.id in existingIds }
                        if (appended.isNotEmpty()) {
                            _state.value = UiState.Success(current + appended)
                        }
                    }
                }
                .onFailure {
                    // 加载更多失败就停在当前页，用户可以继续滑动或重新进页面。
                    endReached = true
                }
            _loadingMore.value = false
        }
    }
}

/** 把异常翻译成用户能看懂的一句话。 */
internal fun Throwable.toFriendlyMessage(): String = when (this) {
    is IOException -> "网络连接失败，请检查网络后重试"
    is HttpException -> "服务器返回 ${code()}，请稍后重试"
    else -> message ?: "加载失败，请重试"
}
