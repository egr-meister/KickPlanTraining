package com.kickplan.training.data.remote

import com.kickplan.training.BuildConfig
import com.kickplan.training.data.DemoData
import com.kickplan.training.data.model.FootballApiResult
import com.kickplan.training.data.model.MatchSource
import com.kickplan.training.data.model.NormalizedMatch
import com.kickplan.training.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Isolated repository for the football-data.org API.
 *
 * Guarantees:
 *  - Reads token + base URL from BuildConfig only (never hardcoded).
 *  - Never throws into the UI; always returns a stable FootballApiResult.
 *  - Never logs the raw API token.
 *  - Returns demo data when the token is missing/placeholder.
 */
class FootballDataRepository {

    private val token: String = BuildConfig.FOOTBALL_DATA_API_TOKEN
    private val baseUrl: String = normalizeBaseUrl(BuildConfig.FOOTBALL_API_BASE_URL)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private fun normalizeBaseUrl(url: String): String {
        val safe = url.ifBlank { "https://api.football-data.org/v4" }
        return if (safe.endsWith("/")) safe else "$safe/"
    }

    private fun tokenIsConfigured(): Boolean =
        token.isNotBlank() && token != "your_api_token_here"

    private val api: FootballDataApiService by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val builder = chain.request().newBuilder()
                // Attach the auth header only when a real token exists.
                if (tokenIsConfigured()) {
                    builder.header("X-Auth-Token", token)
                }
                chain.proceed(builder.build())
            }
            .build()

        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(FootballDataApiService::class.java)
    }

    /**
     * Fetch matches. Optional filters are applied only when non-blank.
     * Always returns a FootballApiResult; never throws.
     */
    suspend fun fetchMatches(
        dateFrom: String = "",
        dateTo: String = "",
        competitionCode: String = ""
    ): FootballApiResult = withContext(Dispatchers.IO) {
        if (!tokenIsConfigured()) {
            return@withContext FootballApiResult(
                ok = true,
                matches = DemoData.demoMatches(),
                error = DemoData.NO_TOKEN_MESSAGE,
                usedDemoData = true
            )
        }

        try {
            val response = api.getMatches(
                dateFrom = dateFrom.ifBlank { null },
                dateTo = dateTo.ifBlank { null },
                competitions = competitionCode.ifBlank { null }
            )

            if (!response.isSuccessful) {
                val message = when (response.code()) {
                    429 -> "API request limit reached. Please try again later."
                    403, 401 -> "API access was refused. Check your API token or plan."
                    in 500..599 -> "The match service is temporarily unavailable."
                    else -> "Could not load the latest matches (code ${response.code()})."
                }
                return@withContext FootballApiResult(
                    ok = false,
                    matches = emptyList(),
                    error = message,
                    usedDemoData = false
                )
            }

            val body = response.body()
            val normalized = normalize(body)
            FootballApiResult(
                ok = true,
                matches = normalized,
                error = "",
                usedDemoData = false
            )
        } catch (e: SocketTimeoutException) {
            FootballApiResult(false, emptyList(), "The request timed out. Check your connection.", false)
        } catch (e: IOException) {
            FootballApiResult(false, emptyList(), "No internet connection available.", false)
        } catch (e: Exception) {
            // Includes serialization / unexpected response shape.
            FootballApiResult(false, emptyList(), "The match data could not be read.", false)
        }
    }

    /** Convert the API DTO into safe NormalizedMatch objects. */
    private fun normalize(body: MatchesResponseDto?): List<NormalizedMatch> {
        val matches = body?.matches ?: return emptyList()
        return matches.mapNotNull { dto ->
            try {
                val (date, time) = DateUtils.splitIsoUtc(dto.utcDate)
                NormalizedMatch(
                    id = dto.id?.toString() ?: "",
                    utcDate = dto.utcDate.orEmpty(),
                    date = date,
                    time = time,
                    competitionName = dto.competition?.name?.takeIf { it.isNotBlank() } ?: "Unknown",
                    competitionCode = dto.competition?.code.orEmpty(),
                    homeTeam = dto.homeTeam?.name?.takeIf { it.isNotBlank() } ?: "Unknown",
                    awayTeam = dto.awayTeam?.name?.takeIf { it.isNotBlank() } ?: "Unknown",
                    status = dto.status?.takeIf { it.isNotBlank() } ?: "SCHEDULED",
                    homeScore = dto.score?.fullTime?.home,
                    awayScore = dto.score?.fullTime?.away,
                    winner = dto.score?.winner.orEmpty(),
                    source = MatchSource.Api
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
