package com.wzbx.cinetrack.data

import com.wzbx.cinetrack.data.local.WatchDao
import com.wzbx.cinetrack.data.local.WatchEntryEntity
import com.wzbx.cinetrack.data.local.WatchItem
import com.wzbx.cinetrack.data.local.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * 看单仓库：界面只跟它打交道，不直接碰 DAO 和 Room。
 *
 * 这样以后换数据源（比如先读网络再落库）时，界面代码不用改。
 */
class WatchRepository(private val dao: WatchDao) {

    fun observeMovies() = dao.observeMovies()

    fun observeWatchlist(): Flow<List<WatchItem>> = dao.observeWatchlist()

    fun observeWatchlistMovieIds(): Flow<List<Long>> = dao.observeWatchlistMovieIds()

    /** 首次启动塞入内置片库；已经有数据就不动，避免覆盖用户数据。 */
    suspend fun ensureSeeded() {
        if (dao.movieCount() == 0) {
            dao.upsertMovies(SampleCatalog.movies)
        }
    }

    /**
     * 加入看单。
     *
     * 已经在看单里就不重复插入，只更新时间——movieId 上的唯一索引是最后一道保险。
     */
    suspend fun addToWatchlist(movieId: Long) {
        val now = System.currentTimeMillis()
        val existing = dao.findEntry(movieId)
        val entry = existing?.copy(updatedAt = now)
            ?: WatchEntryEntity(
                movieId = movieId,
                status = WatchStatus.WANT,
                addedAt = now,
                updatedAt = now
            )
        dao.upsertEntry(entry)
    }

    suspend fun removeFromWatchlist(movieId: Long) {
        dao.removeByMovieId(movieId)
    }

    /** 状态按 想看 → 在看 → 看过 → 想看 循环，按钮点一下就推进一格。 */
    suspend fun advanceStatus(id: Long, current: WatchStatus) {
        val next = when (current) {
            WatchStatus.WANT -> WatchStatus.WATCHING
            WatchStatus.WATCHING -> WatchStatus.WATCHED
            WatchStatus.WATCHED -> WatchStatus.WANT
        }
        dao.updateStatus(id, next, System.currentTimeMillis())
    }
}
