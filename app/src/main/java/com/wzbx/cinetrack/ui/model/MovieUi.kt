package com.wzbx.cinetrack.ui.model

/** 列表项需要的字段。 */
data class MovieUi(
    val id: Long,
    val title: String,
    val year: String,
    val rating: Double,
    val posterUrl: String?
)

/** 详情页需要的字段，比列表多了简介、片长、类型。 */
data class MovieDetailUi(
    val id: Long,
    val title: String,
    val year: String,
    val rating: Double,
    val posterUrl: String?,
    val overview: String,
    val runtimeMinutes: Int,
    val genres: String,
    val releaseDate: String?
)
