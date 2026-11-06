package com.wzbx.cinetrack.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/** 看单状态。 */
enum class WatchStatus(val label: String) {
    WANT("想看"),
    WATCHING("在看"),
    WATCHED("看过")
}

/**
 * 影片表。
 *
 * 阶段 B 的数据来自内置片库，阶段 C 接 TMDB 后主键继续用对方的 id，
 * 这样重复加入同一部片不会产生两条记录。
 */
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val overview: String = "",
    /** 上映日期，yyyy-MM-dd；未知时为空。 */
    val releaseDate: String? = null,
    val runtimeMinutes: Int = 0,
    /** 类型用逗号分隔，阶段 C 再决定要不要拆成关联表。 */
    val genres: String = "",
    val voteAverage: Double = 0.0,
    val posterPath: String? = null
)

/**
 * 看单表。
 *
 * 一部影片在看单里只允许有一条记录（movieId 唯一），
 * 状态变化是更新而不是再插一条，避免出现「同一部片既是想看又是看过」。
 */
@Entity(
    tableName = "watch_entries",
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["movieId"], unique = true), Index("status")]
)
data class WatchEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val movieId: Long,
    val status: WatchStatus,
    val rating: Double? = null,
    val note: String = "",
    val addedAt: Long,
    val updatedAt: Long
)

/** 看单条目 + 影片信息，界面直接消费这个对象。 */
data class WatchItem(
    @Embedded val entry: WatchEntryEntity,
    @Relation(parentColumn = "movieId", entityColumn = "id")
    val movie: MovieEntity
)
