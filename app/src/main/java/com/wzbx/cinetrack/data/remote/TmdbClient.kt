package com.wzbx.cinetrack.data.remote

import com.wzbx.cinetrack.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Retrofit 单例：整个 App 共用一个 OkHttp 连接池。 */
object TmdbClient {

    private val json = Json {
        ignoreUnknownKeys = true   // 接口新增字段不影响解析
        coerceInputValues = true   // null 落到带默认值的字段上
    }

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val url = chain.request().url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
            chain.proceed(chain.request().newBuilder().url(url).build())
        }
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }
        )
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val api: TmdbApi = Retrofit.Builder()
        .baseUrl(BuildConfig.TMDB_BASE_URL)
        .client(okHttp)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(TmdbApi::class.java)
}
