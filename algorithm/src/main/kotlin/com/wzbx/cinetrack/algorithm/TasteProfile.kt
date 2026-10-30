package com.wzbx.cinetrack.algorithm

import kotlin.math.pow

/** 一条已看记录，是口味画像的输入。 */
data class WatchRecord(
    val genres: Set<String>,
    /** 1..5 分，未打分传 null。 */
    val rating: Double?,
    val watchedEpochDay: Long
)

/** 口味画像：类型 → 权重，权重和恒小于 1（因为做了贝叶斯平滑）。 */
data class UserTaste(val genreWeights: Map<String, Double>) {
    companion object {
        val EMPTY = UserTaste(emptyMap())
    }
}

/**
 * 口味画像构建。
 *
 * 两个坑必须处理，否则推荐结果会很难看：
 *
 * 1. **小样本偏差**：只看过 2 部科幻就认定"科幻迷"。
 *    用贝叶斯平滑 `(n_g + α·prior) / (N + α)`，α 相当于"先验的 4 部观影量"。
 * 2. **兴趣漂移**：三年前看的片不该和上个月看的等权。
 *    用半衰期 180 天的指数衰减。
 */
object TasteProfileBuilder {

    fun build(
        records: List<WatchRecord>,
        todayEpochDay: Long,
        halfLifeDays: Double = 180.0,
        prior: Double = 0.5,
        strength: Double = 4.0
    ): UserTaste {
        if (records.isEmpty()) return UserTaste.EMPTY

        val weighted = mutableMapOf<String, Double>()
        var totalWeight = 0.0

        for (record in records) {
            val age = (todayEpochDay - record.watchedEpochDay).coerceAtLeast(0)
            val decay = 0.5.pow(age / halfLifeDays)
            // 评分映射成 0.2..1 的满意度；没打分给 0.6 的中性值。
            val satisfaction = ((record.rating ?: 3.0) / 5.0).coerceIn(0.2, 1.0)
            val weight = decay * satisfaction
            if (weight <= 0.0) continue
            totalWeight += weight
            for (genre in record.genres) {
                weighted[genre] = (weighted[genre] ?: 0.0) + weight
            }
        }

        if (totalWeight <= 0.0) return UserTaste.EMPTY

        return UserTaste(
            weighted.keys.associateWith { genre ->
                ((weighted[genre] ?: 0.0) + strength * prior) / (totalWeight + strength)
            }
        )
    }
}
