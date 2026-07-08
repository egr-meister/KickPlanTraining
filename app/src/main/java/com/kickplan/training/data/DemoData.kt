package com.kickplan.training.data

import com.kickplan.training.data.model.MatchSource
import com.kickplan.training.data.model.NormalizedMatch
import com.kickplan.training.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Local demo match data. Used when no API token is configured, or when the API
 * call fails and there is no cache. Contains only generic, plain-text team and
 * competition names -- no official logos, no real player data, no branding.
 */
object DemoData {

    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun dayFromNow(offset: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offset)
        return df.format(cal.time)
    }

    fun demoMatches(): List<NormalizedMatch> = listOf(
        NormalizedMatch(
            id = "demo-1",
            utcDate = "",
            date = dayFromNow(1),
            time = "18:30",
            competitionName = "Demo League",
            competitionCode = "DL",
            homeTeam = "Riverside United",
            awayTeam = "Hill Park FC",
            status = "SCHEDULED",
            homeScore = null,
            awayScore = null,
            winner = "",
            source = MatchSource.Demo
        ),
        NormalizedMatch(
            id = "demo-2",
            date = dayFromNow(2),
            time = "20:00",
            competitionName = "Demo League",
            competitionCode = "DL",
            homeTeam = "North Coast City",
            awayTeam = "Old Town Rovers",
            status = "SCHEDULED",
            source = MatchSource.Demo
        ),
        NormalizedMatch(
            id = "demo-3",
            date = dayFromNow(3),
            time = "16:00",
            competitionName = "Demo Cup",
            competitionCode = "DC",
            homeTeam = "Green Valley",
            awayTeam = "Seaside Athletic",
            status = "SCHEDULED",
            source = MatchSource.Demo
        ),
        NormalizedMatch(
            id = "demo-4",
            date = dayFromNow(-1),
            time = "19:45",
            competitionName = "Demo League",
            competitionCode = "DL",
            homeTeam = "Riverside United",
            awayTeam = "Green Valley",
            status = "FINISHED",
            homeScore = 2,
            awayScore = 1,
            winner = "HOME_TEAM",
            source = MatchSource.Demo
        ),
        NormalizedMatch(
            id = "demo-5",
            date = dayFromNow(5),
            time = "17:15",
            competitionName = "Demo Cup",
            competitionCode = "DC",
            homeTeam = "Hill Park FC",
            awayTeam = "North Coast City",
            status = "SCHEDULED",
            source = MatchSource.Demo
        )
    )

    /** Friendly text shown when demo data is presented because no token exists. */
    const val NO_TOKEN_MESSAGE =
        "API token is not configured. Showing demo matches."
}
