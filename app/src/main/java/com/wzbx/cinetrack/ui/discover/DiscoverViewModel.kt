package com.wzbx.cinetrack.ui.discover

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wzbx.cinetrack.CineTrackApplication
import com.wzbx.cinetrack.data.local.MovieEntity
import com.wzbx.cinetrack.ui.components.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 发现页的 ViewModel。
 *
 * 数据来自 Room 的 Flow，数据库一变界面自动刷新，
 * 不需要手动 load()，也不会出现「加入看单后列表不更新」。
 */
class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CineTrackApplication).repository

    val state: StateFlow<UiState<List<MovieEntity>>> = repository.observeMovies()
        .map { movies -> if (movies.isEmpty()) UiState.Empty else UiState.Success(movies) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    val watchlistIds: StateFlow<Set<Long>> = repository.observeWatchlistMovieIds()
        .map { it.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptySet()
        )

    fun addToWatchlist(movieId: Long) {
        viewModelScope.launch { repository.addToWatchlist(movieId) }
    }
}
