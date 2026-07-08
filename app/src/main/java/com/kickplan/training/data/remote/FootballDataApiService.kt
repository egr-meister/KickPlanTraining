package com.kickplan.training.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the football-data.org API v4.
 *
 * Only the /matches endpoint is used. No odds, predictions, bookmaker or
 * betting endpoints are ever called.
 */
interface FootballDataApiService {

    @GET("matches")
    suspend fun getMatches(
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("competitions") competitions: String? = null
    ): Response<MatchesResponseDto>
}
