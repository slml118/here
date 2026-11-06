package com.wzbx.cinetrack.data

import com.wzbx.cinetrack.BuildConfig
import com.wzbx.cinetrack.data.remote.TmdbApi
import com.wzbx.cinetrack.data.remote.TmdbClient
import com.wzbx.cinetrack.data.remote.TmdbMovieDetail
import com.wzbx.cinetrack.data.remote.TmdbMovieDto
import com.wzbx.cinetrack.ui.model.MovieDetailUi
import com.wzbx.cinetrack.ui.model.MovieUi

/**
 * 远程数据仓库：负责调接口 + 把 DTO 转成界面模型。
 *
 * 界面层不接触网络 DTO，接口字段改名时只改这里。
 */
class MovieRepository(private val api: TmdbApi = TmdbClient.api) {

    suspend fun trending(): List<MovieUi> =
        api.trending().results.map { it.toUi() }

    suspend fun search(query: String): List<MovieUi> {
        if (query.isBlank()) return emptyList()
        return api.search(query.trim()).results.map { it.toUi() }
    }

    suspend fun detail(movieId: Long): MovieDetailUi = api.detail(movieId).toUi()

    private fun TmdbMovieDto.toUi() = MovieUi(
        id = id,
        title = title.ifBlank { originalTitle },
        year = releaseDate?.take(4).orEmpty().ifBlank { "待定" },
        rating = voteAverage,
        posterUrl = posterPath?.let { BuildConfig.TMDB_IMAGE_BASE + it }
    )

    private fun TmdbMovieDetail.toUi() = MovieDetailUi(
        id = id,
        title = title,
        year = releaseDate?.take(4).orEmpty().ifBlank { "待定" },
        rating = voteAverage,
        posterUrl = posterPath?.let { BuildConfig.TMDB_IMAGE_BASE + it },
        overview = overview.ifBlank { "暂无简介" },
        runtimeMinutes = runtime ?: 0,
        genres = genres.joinToString(" / ") { it.name }.ifBlank { "类型未知" },
        releaseDate = releaseDate
    )
}
