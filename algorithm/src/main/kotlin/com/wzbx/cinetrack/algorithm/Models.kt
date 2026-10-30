package com.wzbx.cinetrack.algorithm

/**
 * 影片的轻量特征。
 *
 * 刻意不依赖任何 Android 类型：算法模块是纯 Kotlin/JVM，
 * 单元测试不需要模拟器，也不需要 Robolectric。
 */
data class MovieFeatures(
    val id: Long,
    val title: String,
    /** 类型集合，如 "科幻"、"悬疑"。 */
    val genres: Set<String>,
    /** 上映日，以 1970-01-01 为原点的天数，便于直接做日期减法。 */
    val releaseEpochDay: Long,
    /** 片长（分钟），0 表示未知。 */
    val runtimeMinutes: Int,
    /** 用户期待度 0..1；加入看单时由用户评分映射而来。 */
    val userAnticipation: Double,
    /** 加入看单的日期。 */
    val addedEpochDay: Long
)

/** 打分因子。用枚举而不是字符串，避免拼写错误，也方便在 UI 上做稳定排序。 */
enum class ScoreFactor(val label: String) {
    ANTICIPATION("期待度"),
    PROXIMITY("上映临近"),
    GENRE_MATCH("口味匹配"),
    DURATION_FIT("时长适配"),
    FRESHNESS("新鲜度")
}

/**
 * 打分权重。
 *
 * 权重存在 DataStore 里，用户可以调整；[normalized] 保证总和恒为 1，
 * 这样总分天然落在 0..1，UI 上可以直接当成百分比展示。
 */
data class ScoreWeights(
    val anticipation: Double = 0.30,
    val proximity: Double = 0.25,
    val genreMatch: Double = 0.20,
    val durationFit: Double = 0.15,
    val freshness: Double = 0.10
) {
    fun of(factor: ScoreFactor): Double = when (factor) {
        ScoreFactor.ANTICIPATION -> anticipation
        ScoreFactor.PROXIMITY -> proximity
        ScoreFactor.GENRE_MATCH -> genreMatch
        ScoreFactor.DURATION_FIT -> durationFit
        ScoreFactor.FRESHNESS -> freshness
    }

    fun normalized(): ScoreWeights {
        val sum = ScoreFactor.entries.sumOf { of(it) }
        // 全 0 是用户误操作，回退到默认权重，避免整列表得分为 0 且无法解释。
        if (sum <= 0.0) return ScoreWeights()
        return ScoreWeights(
            anticipation = anticipation / sum,
            proximity = proximity / sum,
            genreMatch = genreMatch / sum,
            durationFit = durationFit / sum,
            freshness = freshness / sum
        )
    }
}

/**
 * 单部影片的打分结果。
 *
 * [parts] 保留每个因子的原始分，UI 可以展开显示"为什么它排第一"，
 * 这是答辩时解释算法最有力的证据。
 */
data class ScoreBreakdown(
    val movieId: Long,
    val total: Double,
    val parts: Map<ScoreFactor, Double>,
    val reasons: List<String>
)
