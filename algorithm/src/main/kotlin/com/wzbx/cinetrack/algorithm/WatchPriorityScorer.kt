package com.wzbx.cinetrack.algorithm

import kotlin.math.exp

/**
 * 看单优先级打分器。
 *
 * 总分 = Σ 权重ᵢ × 因子ᵢ，所有因子都归一化到 0..1：
 *
 * - 期待度：用户自己给的分数，直接采用；
 * - 上映临近：未上映用指数衰减 `e^(-d/14)`，已上映的在 60 天内线性衰减；
 * - 口味匹配：影片类型在用户口味画像中的相对权重均值；
 * - 时长适配：以 110 分钟为中心的钟形曲线，太长太短都降分；
 * - 新鲜度：加入看单越久越不新鲜，半衰期 30 天。
 *
 * 选指数/钟形而不是线性，是因为"临近上映"和"刚加入"这两件事在体感上是
 * 前几天变化剧烈、后面趋于平缓，线性函数会把 30 天和 60 天算得过于接近。
 */
object WatchPriorityScorer {

    private const val TAU_DAYS = 14.0
    private const val RELEASED_FADE_DAYS = 60.0
    private const val RELEASED_WEIGHT = 0.6
    private const val IDEAL_RUNTIME = 110.0
    private const val RUNTIME_SIGMA = 45.0
    private const val FRESH_TAU_DAYS = 30.0

    fun score(
        movie: MovieFeatures,
        taste: UserTaste,
        todayEpochDay: Long,
        weights: ScoreWeights = ScoreWeights()
    ): ScoreBreakdown {
        val w = weights.normalized()
        val parts = mapOf(
            ScoreFactor.ANTICIPATION to movie.userAnticipation.coerceIn(0.0, 1.0),
            ScoreFactor.PROXIMITY to proximity(movie.releaseEpochDay - todayEpochDay),
            ScoreFactor.GENRE_MATCH to genreMatch(movie.genres, taste),
            ScoreFactor.DURATION_FIT to durationFit(movie.runtimeMinutes),
            ScoreFactor.FRESHNESS to freshness(movie.addedEpochDay, todayEpochDay)
        )
        val total = parts.entries.sumOf { (factor, value) -> value * w.of(factor) }
        return ScoreBreakdown(movie.id, total, parts, buildReasons(parts, w))
    }

    /** 距上映还有 [daysUntil] 天；负数表示已经上映。 */
    fun proximity(daysUntil: Long): Double = when {
        daysUntil >= 0 -> exp(-daysUntil / TAU_DAYS)
        else -> ((1.0 - (-daysUntil) / RELEASED_FADE_DAYS).coerceIn(0.0, 1.0)) * RELEASED_WEIGHT
    }

    /**
     * 口味匹配：取影片各类型在画像中的相对权重（最大值归一化到 1）再求平均。
     *
     * 没有画像或类型未知时返回 0.5 的中性值，而不是 0——
     * 否则新用户的第一部影片会被所有因子同时判低分。
     */
    fun genreMatch(genres: Set<String>, taste: UserTaste): Double {
        if (genres.isEmpty() || taste.genreWeights.isEmpty()) return 0.5
        val maxWeight = taste.genreWeights.values.max()
        if (maxWeight <= 0.0) return 0.5
        return genres
            .map { genre -> (taste.genreWeights[genre] ?: 0.0) / maxWeight }
            .average()
            .coerceIn(0.0, 1.0)
    }

    private fun durationFit(minutes: Int): Double {
        if (minutes <= 0) return 0.5
        val z = (minutes - IDEAL_RUNTIME) / RUNTIME_SIGMA
        return exp(-0.5 * z * z)
    }

    private fun freshness(addedEpochDay: Long, todayEpochDay: Long): Double {
        val age = (todayEpochDay - addedEpochDay).coerceAtLeast(0)
        return exp(-age / FRESH_TAU_DAYS)
    }

    /** 取贡献最大的两个因子生成一句人话，直接显示在列表项下面。 */
    private fun buildReasons(parts: Map<ScoreFactor, Double>, weights: ScoreWeights): List<String> =
        parts.entries
            .sortedByDescending { (factor, value) -> value * weights.of(factor) }
            .take(2)
            .filter { (_, value) -> value > 0.05 }
            .map { (factor, value) -> "${factor.label} ${(value * 100).toInt()} 分" }
}
