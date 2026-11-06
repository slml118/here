package com.wzbx.cinetrack.data.remote

import android.util.Log
import com.wzbx.cinetrack.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/** Retrofit 单例：整个 App 共用一个 OkHttp 连接池。 */
object TmdbClient {

    private const val TAG = "Tmdb"

    private val json = Json {
        ignoreUnknownKeys = true   // 接口新增字段不影响解析
        coerceInputValues = true   // null 落到带默认值的字段上
    }

    private val okHttp: OkHttpClient = OkHttpClient.Builder()
        // api_key 统一在这里追加，不在每个接口方法里重复写。
        .addInterceptor { chain ->
            val url = chain.request().url.newBuilder()
                .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .build()
            chain.proceed(chain.request().newBuilder().url(url).build())
        }
        // 只记「方法 + 路径 + 状态码」，不记 query —— 免得 api_key 出现在 logcat 里。
        .addInterceptor { chain ->
            val request = chain.request()
            val start = System.currentTimeMillis()
            val response = chain.proceed(request)
            if (BuildConfig.DEBUG) {
                Log.d(
                    TAG,
                    "${request.method} ${request.url.encodedPath} -> ${response.code} " +
                        "(${System.currentTimeMillis() - start}ms)"
                )
            }
            response
        }
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
