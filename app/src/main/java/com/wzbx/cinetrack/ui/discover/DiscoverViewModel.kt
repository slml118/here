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
 * 发现页：一次性拉取「本周热门」。
 *
 * 失败不做缓存兜底，直接把错误交给界面显示 + 重试——
 * 这样用户看到的就是真实状态，不会出现「看起来有数据其实全是旧的」。
 */
class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository()

    private val _state = MutableStateFlow<UiState<List<MovieUi>>>(UiState.Loading)
    val state: StateFlow<UiState<List<MovieUi>>> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { repository.trending() }
                .onSuccess { movies ->
                    _state.value = if (movies.isEmpty()) UiState.Empty else UiState.Success(movies)
                }
                .onFailure { error ->
                    _state.value = UiState.Error(error.toFriendlyMessage())
                }
        }
    }
}

/** 把异常翻译成用户能看懂的一句话。 */
internal fun Throwable.toFriendlyMessage(): String = when (this) {
    is IOException -> "网络连接失败，请检查网络后重试"
    is HttpException -> "服务器返回 ${code()}，请稍后重试"
    else -> message ?: "加载失败，请重试"
}
