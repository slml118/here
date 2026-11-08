package com.wzbx.cinetrack.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wzbx.cinetrack.data.MovieRepository
import com.wzbx.cinetrack.ui.components.UiState
import com.wzbx.cinetrack.ui.discover.toFriendlyMessage
import com.wzbx.cinetrack.ui.model.MovieUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 搜索页：按片名查 TMDB，支持继续翻页。
 *
 * 没有做输入即搜（防抖）——课程项目里多一层 debounce 就多一个要解释的东西，
 * 而且每敲一个字就打一次接口对免费额度也不友好。改成按键盘搜索键触发。
 */
class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _state = MutableStateFlow<UiState<List<MovieUi>>>(UiState.Empty)
    val state: StateFlow<UiState<List<MovieUi>>> = _state.asStateFlow()

    /** 区分「还没搜过」和「搜了但没结果」，空态文案不一样。 */
    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private val _loadingMore = MutableStateFlow(false)
    val loadingMore: StateFlow<Boolean> = _loadingMore.asStateFlow()

    private var page = 0
    private var endReached = false
    private var currentQuery = ""

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun search() {
        val keyword = _query.value.trim()
        if (keyword.isEmpty()) {
            _state.value = UiState.Empty
            _hasSearched.value = false
            return
        }

        currentQuery = keyword
        page = 0
        endReached = false
        _hasSearched.value = true
        _state.value = UiState.Loading

        viewModelScope.launch {
            runCatching { repository.search(currentQuery, page = 1) }
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

    /** 结果列表滑到底时继续搜下一页。 */
    fun loadMore() {
        val current = (_state.value as? UiState.Success)?.data ?: return
        if (_loadingMore.value || endReached || currentQuery.isEmpty()) return

        _loadingMore.value = true
        viewModelScope.launch {
            runCatching { repository.search(currentQuery, page = page + 1) }
                .onSuccess { result ->
                    page = result.page
                    endReached = result.movies.isEmpty() || result.page >= result.totalPages
                    if (result.movies.isNotEmpty()) {
                        val existingIds = current.mapTo(HashSet()) { it.id }
                        val appended = result.movies.filterNot { it.id in existingIds }
                        if (appended.isNotEmpty()) {
                            _state.value = UiState.Success(current + appended)
                        }
                    }
                }
                .onFailure { endReached = true }
            _loadingMore.value = false
        }
    }

    /** 点「重试」时重新搜当前关键词。 */
    fun retry() = search()
}
