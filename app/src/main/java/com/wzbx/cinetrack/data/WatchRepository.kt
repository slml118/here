package com.wzbx.cinetrack.data

import com.wzbx.cinetrack.data.local.MovieEntity
import com.wzbx.cinetrack.data.local.WatchDao
import com.wzbx.cinetrack.data.local.WatchEntryEntity
import com.wzbx.cinetrack.data.local.WatchItem
import com.wzbx.cinetrack.data.local.WatchStatus
import kotlinx.coroutines.flow.Flow

/**
 * 看单仓库：界面只跟它打交道，不直接碰 DAO 和 Room。
 */
class WatchRepository(private val dao: WatchDao) {

    fun observeWatchlist(): Flow<List<WatchItem>> = dao.observeWatchlist()

    fun observeWatchlistMovieIds(): Flow<List<Long>> = dao.observeWatchlistMovieIds()

    suspend fun isInWatchlist(movieId: Long): Boolean = dao.findEntry(movieId) != null

    /**
     * 加入看单。
     *
     * 先 upsert 影片行，再写看单——因为 watch_entries 有外键指向 movies，
     * 顺序反了会直接触发外键约束失败。
     */
    suspend fun addToWatchlist(movie: MovieEntity) {
        dao.upsertMovies(listOf(movie))

        val now = System.currentTimeMillis()
        val existing = dao.findEntry(movie.id)
        val entry = existing?.copy(updatedAt = now)
            ?: WatchEntryEntity(
                movieId = movie.id,
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
