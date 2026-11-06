package com.wzbx.cinetrack.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * TMDB 接口声明。
 *
 * api_key 不写在每个方法上，而是由 OkHttp 拦截器统一追加，
 * 这样换 key 或改成 Bearer Token 只动一个地方。
 */
interface TmdbApi {

    /** 本周热门。 */
    @GET("trending/movie/week")
    suspend fun trending(
        @Query("language") language: String = "zh-CN",
        @Query("page") page: Int = 1
    ): TmdbMovieList

    /** 影片详情。 */
    @GET("movie/{movieId}")
    suspend fun detail(
        @Path("movieId") movieId: Long,
        @Query("language") language: String = "zh-CN"
    ): TmdbMovieDetail

    /** 按片名搜索。 */
    @GET("search/movie")
    suspend fun search(
        @Query("query") query: String,
        @Query("language") language: String = "zh-CN",
        @Query("page") page: Int = 1
    ): TmdbMovieList
}
