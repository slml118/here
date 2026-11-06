package com.wzbx.cinetrack.ui.watchlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wzbx.cinetrack.CineTrackApplication
import com.wzbx.cinetrack.data.local.WatchItem
import com.wzbx.cinetrack.ui.components.UiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WatchlistViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CineTrackApplication).repository

    val state: StateFlow<UiState<List<WatchItem>>> = repository.observeWatchlist()
        .map { items -> if (items.isEmpty()) UiState.Empty else UiState.Success(items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )

    fun advanceStatus(item: WatchItem) {
        viewModelScope.launch {
            repository.advanceStatus(item.entry.id, item.entry.status)
        }
    }

    fun remove(movieId: Long) {
        viewModelScope.launch { repository.removeFromWatchlist(movieId) }
    }
}
