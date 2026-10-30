package com.wzbx.cinetrack.algorithm

import java.time.LocalDate
import java.util.Locale

/** 年度报告的输入：一条已看记录。 */
data class AnnualRecord(
    val title: String,
    val genres: Set<String>,
    val rating: Double?,
    val watchedEpochDay: Long,
    val runtimeMinutes: Int
)

/** 年度报告的输出。字段都是"算好的"，UI 只负责画。 */
data class AnnualReport(
    val year: Int,
    val watchedCount: Int,
    val totalMinutes: Int,
    val averageRating: Double,
    val topGenres: List<Pair<String, Int>>,
    val busiestMonth: Int?,
    val longestStreakDays: Int,
    val insights: List<String>
)

/**
 * 年度报告生成器。
 *
 * 统计部分很直接，真正体现"业务规则"的是 [insights]：
 * 同一堆数据，怎么变成用户愿意看的一句话。这里用规则引擎而不是模板填空，
 * 每条规则都有明确阈值，答辩时能逐条解释为什么是 40%、4.2 分。
 */
object AnnualReportGenerator {

    fun generate(year: Int, records: List<AnnualRecord>): AnnualReport {
        val inYear = records.filter { LocalDate.ofEpochDay(it.watchedEpochDay).year == year }

        if (inYear.isEmpty()) {
            return AnnualReport(
                year = year,
                watchedCount = 0,
                totalMinutes = 0,
                averageRating = 0.0,
                topGenres = emptyList(),
                busiestMonth = null,
                longestStreakDays = 0,
                insights = listOf("今年还没有观影记录，先从看单里挑一部开始吧。")
            )
        }

        val totalMinutes = inYear.sumOf { it.runtimeMinutes.coerceAtLeast(0) }
        val rated = inYear.mapNotNull { it.rating }
        val averageRating = if (rated.isEmpty()) 0.0 else rated.average()

        val genreCounts = mutableMapOf<String, Int>()
        inYear.forEach { record ->
            record.genres.forEach { genre -> genreCounts[genre] = (genreCounts[genre] ?: 0) + 1 }
        }
        val topGenres = genreCounts.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key to it.value }

        val monthCounts = inYear
            .groupingBy { LocalDate.ofEpochDay(it.watchedEpochDay).monthValue }
            .eachCount()
        val busiestMonth = monthCounts.maxByOrNull { it.value }?.key

        val longestStreak = longestStreak(inYear.map { it.watchedEpochDay })

        val insights = buildInsights(
            count = inYear.size,
            totalMinutes = totalMinutes,
            averageRating = averageRating,
            ratedCount = rated.size,
            topGenres = topGenres,
            busiestMonth = busiestMonth,
            longestStreak = longestStreak
        )

        return AnnualReport(
            year = year,
            watchedCount = inYear.size,
            totalMinutes = totalMinutes,
            averageRating = averageRating,
            topGenres = topGenres,
            busiestMonth = busiestMonth,
            longestStreakDays = longestStreak,
            insights = insights
        )
    }

    /** 同一天看多部只算一天。 */
    private fun longestStreak(epochDays: List<Long>): Int {
        val days = epochDays.distinct().sorted()
        if (days.isEmpty()) return 0
        var best = 1
        var current = 1
        for (index in 1 until days.size) {
            if (days[index] == days[index - 1] + 1) {
                current++
                if (current > best) best = current
            } else {
                current = 1
            }
        }
        return best
    }

    private fun buildInsights(
        count: Int,
        totalMinutes: Int,
        averageRating: Double,
        ratedCount: Int,
        topGenres: List<Pair<String, Int>>,
        busiestMonth: Int?,
        longestStreak: Int
    ): List<String> {
        val insights = mutableListOf<String>()
        val hours = String.format(Locale.CHINA, "%.1f", totalMinutes / 60.0)
        insights += "今年共看完 $count 部、$totalMinutes 分钟，约 $hours 小时。"

        topGenres.firstOrNull()?.let { (genre, genreCount) ->
            val share = genreCount * 100 / count
            insights += if (share >= 40) {
                "你是 $genre 重度爱好者，$share% 的观影都集中在这个类型。"
            } else {
                "你看得最多的是 $genre，共 $genreCount 部。"
            }
        }

        busiestMonth?.let { insights += "$it 月是你的观影高峰。" }

        if (ratedCount == 0) {
            insights += "今年还没有打分记录，打个分能让年度报告更准确。"
        } else {
            val tone = when {
                averageRating >= 4.2 -> "打分偏宽容"
                averageRating <= 2.8 -> "打分很严格"
                else -> "打分比较中性"
            }
            insights += "平均评分 ${String.format(Locale.CHINA, "%.1f", averageRating)}，$tone。"
        }

        if (longestStreak >= 3) {
            insights += "最长连续观影 $longestStreak 天，习惯很稳定。"
        }
        return insights
    }
}
