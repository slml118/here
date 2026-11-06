package com.wzbx.cinetrack.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wzbx.cinetrack.CineTrackApplication
import com.wzbx.cinetrack.data.MovieRepository
import com.wzbx.cinetrack.data.local.MovieEntity
import com.wzbx.cinetrack.ui.components.UiState
import com.wzbx.cinetrack.ui.discover.toFriendlyMessage
import com.wzbx.cinetrack.ui.model.MovieDetailUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val movieRepository = MovieRepository()
    private val watchRepository = (application as CineTrackApplication).repository

    private val _state = MutableStateFlow<UiState<MovieDetailUi>>(UiState.Loading)
    val state: StateFlow<UiState<MovieDetailUi>> = _state.asStateFlow()

    private val _inWatchlist = MutableStateFlow(false)
    val inWatchlist: StateFlow<Boolean> = _inWatchlist.asStateFlow()

    private var loadedId: Long = -1

    fun load(movieId: Long) {
        if (loadedId == movieId && _state.value is UiState.Success) return
        loadedId = movieId
        viewModelScope.launch {
            _state.value = UiState.Loading
            runCatching { movieRepository.detail(movieId) }
                .onSuccess { detail ->
                    _state.value = UiState.Success(detail)
                    _inWatchlist.value = watchRepository.isInWatchlist(detail.id)
                }
                .onFailure { error ->
                    _state.value = UiState.Error(error.toFriendlyMessage())
                }
        }
    }

    fun addToWatchlist() {
        val detail = (_state.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            watchRepository.addToWatchlist(detail.toEntity())
            _inWatchlist.value = true
        }
    }

    /** 详情模型 → 数据库实体。类型用逗号拼起来，与阶段 B 的表结构保持一致。 */
    private fun MovieDetailUi.toEntity() = MovieEntity(
        id = id,
        title = title,
        overview = overview,
        releaseDate = releaseDate,
        runtimeMinutes = runtimeMinutes,
        genres = genres.replace(" / ", ","),
        voteAverage = rating,
        posterPath = posterUrl
    )
}
