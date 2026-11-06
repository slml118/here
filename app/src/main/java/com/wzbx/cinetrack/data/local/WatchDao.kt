package com.wzbx.cinetrack.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchDao {

    @Query("SELECT * FROM movies ORDER BY voteAverage DESC")
    fun observeMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovies(movies: List<MovieEntity>)

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun movieCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: WatchEntryEntity): Long

    @Query("SELECT * FROM watch_entries WHERE movieId = :movieId LIMIT 1")
    suspend fun findEntry(movieId: Long): WatchEntryEntity?

    @Query("SELECT movieId FROM watch_entries")
    fun observeWatchlistMovieIds(): Flow<List<Long>>

    @Transaction
    @Query("SELECT * FROM watch_entries ORDER BY updatedAt DESC")
    fun observeWatchlist(): Flow<List<WatchItem>>

    @Query("UPDATE watch_entries SET status = :status, updatedAt = :now WHERE id = :id")
    suspend fun updateStatus(id: Long, status: WatchStatus, now: Long)

    @Query("DELETE FROM watch_entries WHERE movieId = :movieId")
    suspend fun removeByMovieId(movieId: Long)
}
