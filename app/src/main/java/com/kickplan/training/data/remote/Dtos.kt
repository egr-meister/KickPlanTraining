package com.kickplan.training.data.remote

import kotlinx.serialization.Serializable

/**
 * DTOs mapping the football-data.org /matches response.
 *
 * Every field is nullable with a default so an unexpected or partial response
 * shape can never crash decoding. `ignoreUnknownKeys` is also enabled on the
 * Json instance, so extra fields from the API are simply skipped.
 */

@Serializable
data class MatchesResponseDto(
    val matches: List<MatchDto>? = null
)

@Serializable
data class MatchDto(
    val id: Long? = null,
    val utcDate: String? = null,
    val status: String? = null,
    val competition: CompetitionDto? = null,
    val homeTeam: TeamDto? = null,
    val awayTeam: TeamDto? = null,
    val score: ScoreDto? = null
)

@Serializable
data class CompetitionDto(
    val id: Long? = null,
    val name: String? = null,
    val code: String? = null
)

@Serializable
data class TeamDto(
    val id: Long? = null,
    val name: String? = null,
    val shortName: String? = null,
    val tla: String? = null
)

@Serializable
data class ScoreDto(
    val winner: String? = null,
    val fullTime: ScoreDetailDto? = null
)

@Serializable
data class ScoreDetailDto(
    val home: Int? = null,
    val away: Int? = null
)
