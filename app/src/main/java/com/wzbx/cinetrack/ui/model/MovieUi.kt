package com.wzbx.cinetrack.ui.model

/** 列表里显示一部影片所需的最小字段。 */
data class MovieUi(
    val id: Long,
    val title: String,
    val genre: String,
    val year: Int,
    val rating: Double
)

/**
 * 阶段 A 的占位数据。
 *
 * 阶段 C 接入 TMDB 之后，这个对象会被真实网络数据替换，
 * 界面层代码不用改——因为它们都只认识 [MovieUi]。
 */
object SampleMovies {
    val list = listOf(
        MovieUi(1, "流浪地球 3", "科幻 / 灾难", 2027, 8.9),
        MovieUi(2, "沙丘：第三部", "科幻 / 冒险", 2027, 8.6),
        MovieUi(3, "坠落的审判", "剧情 / 悬疑", 2026, 8.4),
        MovieUi(4, "蜘蛛侠：纵横宇宙", "动画 / 动作", 2026, 8.7),
        MovieUi(5, "疯狂的麦克斯：狂暴女神", "动作 / 冒险", 2026, 7.9),
        MovieUi(6, "奥本海默", "传记 / 历史", 2026, 8.8)
    )
}
