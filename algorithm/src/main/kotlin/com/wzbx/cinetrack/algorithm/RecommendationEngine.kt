package com.wzbx.cinetrack.algorithm

/**
 * 推荐引擎：先按 [WatchPriorityScorer] 算相关度，再做 MMR 多样性重排。
 *
 * MMR（Maximal Marginal Relevance）：
 *
 *     score = λ · 相关度 − (1 − λ) · 与已选集合的最大相似度
 *
 * 如果不做这一步，看单里 5 部超英片会占满前 5 名，推荐结果毫无信息量。
 * λ 越大越"准"，越小越"杂"；默认 0.7 偏准。
 *
 * 注意：返回的 [ScoreBreakdown.total] 仍然是**相关度**，不是 MMR 值，
 * 列表排序按 MMR，但展示给用户的分数必须可解释。
 */
object RecommendationEngine {

    /** 用 Jaccard 相似度衡量两部影片的类型重合，简单、可解释、无需向量化。 */
    fun similarity(a: Set<String>, b: Set<String>): Double {
        if (a.isEmpty() || b.isEmpty()) return 0.0
        val intersection = a.intersect(b).size.toDouble()
        val union = a.union(b).size.toDouble()
        return intersection / union
    }

    fun recommend(
        candidates: List<MovieFeatures>,
        taste: UserTaste,
        todayEpochDay: Long,
        limit: Int,
        lambda: Double = 0.7,
        weights: ScoreWeights = ScoreWeights()
    ): List<ScoreBreakdown> {
        if (limit <= 0 || candidates.isEmpty()) return emptyList()

        val remaining = candidates
            .map { movie -> movie to WatchPriorityScorer.score(movie, taste, todayEpochDay, weights) }
            .toMutableList()
        val picked = mutableListOf<Pair<MovieFeatures, ScoreBreakdown>>()

        while (picked.size < limit && remaining.isNotEmpty()) {
            var bestIndex = 0
            var bestValue = Double.NEGATIVE_INFINITY
            for (index in remaining.indices) {
                val (movie, score) = remaining[index]
                val maxSimilarity = picked.maxOfOrNull { (chosen, _) ->
                    similarity(movie.genres, chosen.genres)
                } ?: 0.0
                val mmr = lambda * score.total - (1.0 - lambda) * maxSimilarity
                if (mmr > bestValue) {
                    bestValue = mmr
                    bestIndex = index
                }
            }
            picked += remaining.removeAt(bestIndex)
        }

        return picked.map { (_, score) -> score }
    }
}
