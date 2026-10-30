package com.wzbx.cinetrack.algorithm

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * 算法模块单测。
 *
 * 这些用例同时是"算法说明书"：答辩时被问到边界情况，
 * 直接翻到这里比口头解释更有说服力。
 */
class AlgorithmTest {

    private val today = LocalDate.of(2026, 10, 24).toEpochDay()

    private fun movie(
        id: Long,
        genres: Set<String>,
        releaseInDays: Long,
        anticipation: Double = 0.8,
        runtime: Int = 110
    ) = MovieFeatures(
        id = id,
        title = "movie-$id",
        genres = genres,
        releaseEpochDay = today + releaseInDays,
        runtimeMinutes = runtime,
        userAnticipation = anticipation,
        addedEpochDay = today
    )

    @Test
    fun `临近上映的影片得分更高`() {
        val soon = WatchPriorityScorer.score(movie(1, setOf("科幻"), 1), UserTaste.EMPTY, today)
        val later = WatchPriorityScorer.score(movie(2, setOf("科幻"), 40), UserTaste.EMPTY, today)
        assertTrue(soon.total > later.total)
    }

    @Test
    fun `上映越久衰减越多但不会变成负数`() {
        assertTrue(WatchPriorityScorer.proximity(-1) > WatchPriorityScorer.proximity(-30))
        assertEquals(0.0, WatchPriorityScorer.proximity(-365), 1e-9)
    }

    @Test
    fun `没有口味画像时类型匹配取中性值`() {
        assertEquals(0.5, WatchPriorityScorer.genreMatch(setOf("科幻"), UserTaste.EMPTY), 1e-9)
    }

    @Test
    fun `权重归一化之后总和为一`() {
        val normalized = ScoreWeights(1.0, 1.0, 1.0, 1.0, 1.0).normalized()
        assertEquals(0.2, normalized.anticipation, 1e-9)
        assertEquals(1.0, ScoreFactor.entries.sumOf { normalized.of(it) }, 1e-9)
    }

    @Test
    fun `权重全为零时回退默认值而不是全部零分`() {
        val normalized = ScoreWeights(0.0, 0.0, 0.0, 0.0, 0.0).normalized()
        assertEquals(0.30, normalized.anticipation, 1e-9)
    }

    @Test
    fun `只看过一部影片也不会被判定为死忠粉`() {
        val taste = TasteProfileBuilder.build(
            records = listOf(WatchRecord(setOf("科幻"), 5.0, today)),
            todayEpochDay = today
        )
        val weight = taste.genreWeights.getValue("科幻")
        assertTrue("平滑后应小于 1，实际 $weight", weight < 0.9)
        assertTrue("但应高于先验 0.5，实际 $weight", weight > 0.5)
    }

    @Test
    fun `很久以前的观影记录权重会衰减`() {
        val fresh = TasteProfileBuilder.build(
            listOf(WatchRecord(setOf("科幻"), 5.0, today)),
            today
        ).genreWeights.getValue("科幻")
        val old = TasteProfileBuilder.build(
            listOf(WatchRecord(setOf("科幻"), 5.0, today - 720)),
            today
        ).genreWeights.getValue("科幻")
        assertTrue("旧记录权重应更低：fresh=$fresh old=$old", fresh > old)
    }

    @Test
    fun `MMR 会为了多样性放弃同类型的第二名`() {
        val candidates = listOf(
            movie(1, setOf("科幻"), 10, anticipation = 1.0),
            movie(2, setOf("科幻"), 10, anticipation = 0.9),
            movie(3, setOf("爱情"), 10, anticipation = 0.2)
        )
        // lambda = 0 表示只考虑多样性，第二部应选类型完全不同的 C。
        val picked = RecommendationEngine.recommend(candidates, UserTaste.EMPTY, today, limit = 2, lambda = 0.0)
        assertEquals(1L, picked[0].movieId)
        assertEquals(3L, picked[1].movieId)
    }

    @Test
    fun `空记录不会让年度报告崩溃`() {
        val report = AnnualReportGenerator.generate(2026, emptyList())
        assertEquals(0, report.watchedCount)
        assertEquals(null, report.busiestMonth)
        assertEquals(1, report.insights.size)
    }

    @Test
    fun `连续观影天数按自然日去重计算`() {
        val base = LocalDate.of(2026, 3, 1).toEpochDay()
        val records = listOf(
            AnnualRecord("a", setOf("科幻"), 4.0, base, 120),
            AnnualRecord("b", setOf("科幻"), 4.0, base + 1, 100),
            AnnualRecord("c", setOf("喜剧"), 3.0, base + 1, 90),
            AnnualRecord("d", setOf("喜剧"), 3.0, base + 2, 95)
        )
        val report = AnnualReportGenerator.generate(2026, records)
        // 4 条记录里有两条落在同一天，所以观影部数是 4、连续天数是 3。
        assertEquals(3, report.longestStreakDays)
        assertEquals(4, report.watchedCount)
        assertEquals(405, report.totalMinutes)
    }

    @Test
    fun `年度报告只统计目标年份`() {
        val inYear = LocalDate.of(2026, 6, 1).toEpochDay()
        val lastYear = LocalDate.of(2025, 6, 1).toEpochDay()
        val records = listOf(
            AnnualRecord("a", setOf("科幻"), 4.0, inYear, 100),
            AnnualRecord("b", setOf("科幻"), 5.0, lastYear, 100)
        )
        val report = AnnualReportGenerator.generate(2026, records)
        assertEquals(1, report.watchedCount)
        assertEquals(4.0, report.averageRating, 1e-9)
    }
}
