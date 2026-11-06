package com.wzbx.cinetrack.data

import com.wzbx.cinetrack.data.local.MovieEntity

/**
 * 内置片库。
 *
 * 阶段 B 没有网络，靠它把数据库和界面先跑通；
 * 阶段 C 接入 TMDB 后只在「数据库为空且网络不可用」时作为兜底。
 */
object SampleCatalog {
    val movies = listOf(
        MovieEntity(
            id = 101,
            title = "流浪地球 3",
            overview = "太阳氦闪危机之后，人类把希望押在最后一次行星发动机点火上。",
            releaseDate = "2027-02-12",
            runtimeMinutes = 158,
            genres = "科幻,灾难",
            voteAverage = 8.9
        ),
        MovieEntity(
            id = 102,
            title = "沙丘：第三部",
            overview = "厄拉科斯成为帝国中心，保罗必须在预言与权力之间做出选择。",
            releaseDate = "2027-03-20",
            runtimeMinutes = 165,
            genres = "科幻,冒险",
            voteAverage = 8.6
        ),
        MovieEntity(
            id = 103,
            title = "坠落的审判",
            overview = "一场坠楼案把婚姻里的每一句话都变成了证据。",
            releaseDate = "2026-11-08",
            runtimeMinutes = 151,
            genres = "剧情,悬疑",
            voteAverage = 8.4
        ),
        MovieEntity(
            id = 104,
            title = "蜘蛛侠：纵横宇宙",
            overview = "迈尔斯穿越多元宇宙，试图改写每一个蜘蛛侠都无法逃脱的命运。",
            releaseDate = "2026-12-15",
            runtimeMinutes = 140,
            genres = "动画,动作",
            voteAverage = 8.7
        ),
        MovieEntity(
            id = 105,
            title = "疯狂的麦克斯：狂暴女神",
            overview = "荒漠中的复仇从一次掠夺开始，延续了四十年的怒火。",
            releaseDate = "2026-11-22",
            runtimeMinutes = 148,
            genres = "动作,冒险",
            voteAverage = 7.9
        ),
        MovieEntity(
            id = 106,
            title = "奥本海默",
            overview = "一个人造出了足以毁灭世界的武器，然后用余生面对它。",
            releaseDate = "2026-10-30",
            runtimeMinutes = 180,
            genres = "传记,历史",
            voteAverage = 8.8
        )
    )
}
