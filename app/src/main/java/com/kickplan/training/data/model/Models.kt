package com.kickplan.training.data.model

import kotlinx.serialization.Serializable

/**
 * All persistent domain models for KickPlan Training.
 *
 * Every class is annotated with @Serializable and every field has a default
 * value so that decoding older / partial / corrupted JSON never fails: missing
 * fields fall back to safe defaults instead of throwing.
 */

// ---------------------------------------------------------------------------
// Enums
// ---------------------------------------------------------------------------

@Serializable
enum class TrainingType(val label: String) {
    Passing("Passing"),
    Shooting("Shooting"),
    Running("Running"),
    Defense("Defense"),
    Stamina("Stamina"),
    Dribbling("Dribbling"),
    Goalkeeper("Goalkeeper"),
    Recovery("Recovery"),
    Custom("Custom");

    companion object {
        fun fromNameSafe(name: String?): TrainingType =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: Passing
    }
}

@Serializable
enum class Difficulty(val label: String) {
    Easy("Easy"),
    Medium("Medium"),
    Hard("Hard");

    companion object {
        fun fromNameSafe(name: String?): Difficulty =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: Medium
    }
}

@Serializable
enum class Intensity(val label: String) {
    Low("Low"),
    Normal("Normal"),
    High("High");

    companion object {
        fun fromNameSafe(name: String?): Intensity =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: Normal
    }
}

@Serializable
enum class MatchSource {
    Api,
    Cache,
    Demo
}

// ---------------------------------------------------------------------------
// Training session
// ---------------------------------------------------------------------------

@Serializable
data class TrainingSession(
    val id: String = "",
    val date: String = "",           // YYYY-MM-DD
    val time: String = "",           // HH:mm
    val type: TrainingType = TrainingType.Passing,
    val customType: String = "",
    val durationMinutes: Int = 0,
    val difficulty: Difficulty = Difficulty.Medium,
    val intensity: Intensity = Intensity.Normal,
    val notes: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    /** Human-readable type label; uses customType when the type is Custom. */
    val displayType: String
        get() = if (type == TrainingType.Custom && customType.isNotBlank()) customType else type.label
}

// ---------------------------------------------------------------------------
// Match schedule models
// ---------------------------------------------------------------------------

@Serializable
data class NormalizedMatch(
    val id: String = "",
    val utcDate: String = "",
    val date: String = "",           // YYYY-MM-DD
    val time: String = "",           // HH:mm
    val competitionName: String = "Unknown",
    val competitionCode: String = "",
    val homeTeam: String = "Unknown",
    val awayTeam: String = "Unknown",
    val status: String = "SCHEDULED",
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val winner: String = "",
    val source: MatchSource = MatchSource.Demo
)

@Serializable
data class MatchScheduleSettings(
    val apiEnabled: Boolean = true,
    val useDemoData: Boolean = false,
    val dateFrom: String = "",
    val dateTo: String = "",
    val competitionCode: String = ""
)

@Serializable
data class MatchScheduleCache(
    val cachedMatches: List<NormalizedMatch> = emptyList(),
    val lastUpdatedAt: String = "",
    val lastError: String = ""
)

// ---------------------------------------------------------------------------
// Settings & top-level app data
// ---------------------------------------------------------------------------

@Serializable
data class Settings(
    val onboardingCompleted: Boolean = false,
    val compactMode: Boolean = false,
    val favoriteTeam: String = "",
    val matchSchedule: MatchScheduleSettings = MatchScheduleSettings()
)

@Serializable
data class AppData(
    val sessions: List<TrainingSession> = emptyList(),
    val settings: Settings = Settings(),
    val matchScheduleCache: MatchScheduleCache = MatchScheduleCache()
)

// ---------------------------------------------------------------------------
// API result wrapper returned by the football repository to the UI layer.
// Never throws; always returns a stable object.
// ---------------------------------------------------------------------------

data class FootballApiResult(
    val ok: Boolean,
    val matches: List<NormalizedMatch>,
    val error: String,
    val usedDemoData: Boolean
)
