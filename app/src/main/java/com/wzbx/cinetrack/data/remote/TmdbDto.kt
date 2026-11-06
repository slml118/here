package com.wzbx.cinetrack.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 列表接口的返回结构。 */
@Serializable
data class TmdbMovieList(
    val page: Int = 1,
    val results: List<TmdbMovieDto> = emptyList(),
    @SerialName("total_pages") val totalPages: Int = 0,
    @SerialName("total_results") val totalResults: Int = 0
)

/** 列表里的一部影片。 */
@Serializable
data class TmdbMovieDto(
    val id: Long,
    val title: String = "",
    @SerialName("original_title") val originalTitle: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("genre_ids") val genreIds: List<Int> = emptyList()
)

/** 详情接口比列表多出片长和类型名称。 */
@Serializable
data class TmdbMovieDetail(
    val id: Long,
    val title: String = "",
    val overview: String = "",
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    val runtime: Int? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    val genres: List<TmdbGenre> = emptyList()
)

@Serializable
data class TmdbGenre(
    val id: Int = 0,
    val name: String = ""
)
