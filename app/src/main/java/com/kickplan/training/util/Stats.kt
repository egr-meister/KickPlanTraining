package com.kickplan.training.util

import com.kickplan.training.data.model.Difficulty
import com.kickplan.training.data.model.TrainingSession

/**
 * Pure functions computing training statistics from a list of sessions.
 * All functions are null/empty safe and never throw.
 */
object Stats {

    data class DaySummary(
        val date: String,
        val sessionCount: Int,
        val totalMinutes: Int,
        val topType: String,
        val difficultyBreakdown: Map<Difficulty, Int>,
        val typeBreakdown: Map<String, Int>
    )

    data class WeeklyStats(
        val totalSessions: Int,
        val totalMinutes: Int,
        val dailyAverageMinutes: Int,
        val bestDay: String,
        val bestDayMinutes: Int,
        val topFocus: String,
        val hardSessions: Int,
        val streak: Int,
        val perDayMinutes: List<DayMinutes>
    )

    data class DayMinutes(
        val date: String,
        val shortLabel: String,
        val minutes: Int,
        val sessions: Int
    )

    fun sessionsForDate(sessions: List<TrainingSession>, date: String): List<TrainingSession> =
        sessions.filter { it.date == date }

    fun daySummary(sessions: List<TrainingSession>, date: String): DaySummary {
        val day = sessionsForDate(sessions, date)
        val totalMinutes = day.sumOf { it.durationMinutes.coerceAtLeast(0) }
        val typeCounts = day.groupingBy { it.displayType }.eachCount()
        val topType = typeCounts.maxByOrNull { it.value }?.key ?: "-"
        val diffCounts = day.groupingBy { it.difficulty }.eachCount()
        return DaySummary(
            date = date,
            sessionCount = day.size,
            totalMinutes = totalMinutes,
            topType = topType,
            difficultyBreakdown = diffCounts,
            typeBreakdown = typeCounts
        )
    }

    /** Distinct dates that have sessions, most recent first. */
    fun historyDates(sessions: List<TrainingSession>): List<String> =
        sessions.map { it.date }
            .filter { it.isNotBlank() }
            .distinct()
            .sortedDescending()

    fun weeklyStats(sessions: List<TrainingSession>): WeeklyStats {
        val week = DateUtils.currentWeekMondayFirst()
        val weekSet = week.toSet()
        val weekSessions = sessions.filter { it.date in weekSet }

        val perDay = week.map { date ->
            val daySessions = weekSessions.filter { it.date == date }
            DayMinutes(
                date = date,
                shortLabel = DateUtils.shortWeekday(date).ifBlank { "?" },
                minutes = daySessions.sumOf { it.durationMinutes.coerceAtLeast(0) },
                sessions = daySessions.size
            )
        }

        val totalSessions = weekSessions.size
        val totalMinutes = weekSessions.sumOf { it.durationMinutes.coerceAtLeast(0) }
        val activeDays = perDay.count { it.minutes > 0 }
        val dailyAverage = if (activeDays > 0) totalMinutes / activeDays else 0

        val best = perDay.maxByOrNull { it.minutes }
        val bestDay = if (best != null && best.minutes > 0) DateUtils.weekdayName(best.date) else "-"
        val bestDayMinutes = best?.minutes ?: 0

        val topFocus = weekSessions.groupingBy { it.displayType }.eachCount()
            .maxByOrNull { it.value }?.key ?: "-"

        val hardSessions = weekSessions.count { it.difficulty == Difficulty.Hard }

        // Simple streak: consecutive days ending today that have >=1 session.
        var streak = 0
        for (day in perDay.reversed()) {
            if (day.sessions > 0) streak++ else break
        }

        return WeeklyStats(
            totalSessions = totalSessions,
            totalMinutes = totalMinutes,
            dailyAverageMinutes = dailyAverage,
            bestDay = bestDay,
            bestDayMinutes = bestDayMinutes,
            topFocus = topFocus,
            hardSessions = hardSessions,
            streak = streak,
            perDayMinutes = perDay
        )
    }
}
