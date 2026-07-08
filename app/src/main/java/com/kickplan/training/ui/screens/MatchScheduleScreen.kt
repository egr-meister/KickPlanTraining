package com.kickplan.training.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.data.model.NormalizedMatch
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.components.TeamCircle
import com.kickplan.training.ui.components.WhiteCard
import com.kickplan.training.ui.match.MatchUiState
import com.kickplan.training.ui.theme.BrightOrangeAccent
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.PaleOrangePanel
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.StatusGreen
import com.kickplan.training.ui.theme.WarningAmber
import com.kickplan.training.ui.theme.WhiteText

@Composable
fun MatchScheduleScreen(
    state: MatchUiState,
    favoriteTeam: String,
    onLoad: () -> Unit,
    onRefresh: () -> Unit,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit
) {
    // Load cached/demo data once when the screen first appears. No auto polling.
    LaunchedEffect(Unit) { onLoad() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(
            title = "Match Schedule",
            subtitle = "An extra planning tool",
            onBack = onBack,
            actionIcon = Icons.Filled.Settings,
            actionDescription = "Match settings",
            onAction = onOpenSettings
        )

        Column(Modifier.padding(16.dp)) {

            // Disclaimer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PaleOrangePanel, RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Text(
                    "Match data is provided by football-data.org and may depend on your API plan.",
                    color = MatchRedOrange,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // Source + last updated + refresh
            Row(verticalAlignment = Alignment.CenterVertically) {
                SourceBadge(state.sourceLabel)
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    if (state.lastUpdated.isNotBlank()) {
                        Text("Last updated: ${state.lastUpdated}", color = SecondaryGrayText, fontSize = 11.sp)
                    }
                }
                Button(
                    onClick = onRefresh,
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MatchRedOrange, contentColor = WhiteText)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh", modifier = Modifier.width(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Refresh", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            if (state.message.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WarningAmber.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(state.message, color = CardDarkText, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(14.dp))

            when {
                state.isLoading && state.matches.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MatchRedOrange)
                    }
                }

                state.isEmpty -> {
                    WhiteCard {
                        Column {
                            Text("No matches available.", color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Try refreshing or check API settings.", color = SecondaryGrayText, fontSize = 13.sp)
                        }
                    }
                }

                else -> {
                    val fav = favoriteTeam.trim()
                    state.matches.forEach { match ->
                        val isFav = fav.isNotBlank() &&
                            (match.homeTeam.contains(fav, ignoreCase = true) ||
                                match.awayTeam.contains(fav, ignoreCase = true))
                        MatchCard(match = match, highlight = isFav)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SourceBadge(label: String) {
    val color = when (label) {
        "Live data" -> StatusGreen
        "Cached data" -> BrightOrangeAccent
        else -> SecondaryGrayText
    }
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(50)) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun MatchCard(match: NormalizedMatch, highlight: Boolean) {
    WhiteCard {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = buildString {
                        append(match.competitionName)
                        if (match.competitionCode.isNotBlank()) append(" (${match.competitionCode})")
                    },
                    color = SecondaryGrayText,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f)
                )
                if (highlight) {
                    Text("★ Favorite", color = MatchRedOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${match.date.ifBlank { "TBD" }}  ${match.time}".trim(),
                color = CardDarkText,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TeamCircle(match.homeTeam, size = 36)
                Spacer(Modifier.width(10.dp))
                Text(
                    match.homeTeam,
                    color = CardDarkText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                ScoreOrVs(match)
                Text(
                    match.awayTeam,
                    color = CardDarkText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(10.dp))
                TeamCircle(match.awayTeam, size = 36)
            }
            Spacer(Modifier.height(8.dp))
            StatusChip(match.status)
        }
    }
}

@Composable
private fun ScoreOrVs(match: NormalizedMatch) {
    val hasScore = match.homeScore != null && match.awayScore != null
    Text(
        text = if (hasScore) "${match.homeScore} - ${match.awayScore}" else "vs",
        color = if (hasScore) MatchRedOrange else SecondaryGrayText,
        fontWeight = FontWeight.ExtraBold,
        fontSize = if (hasScore) 16.sp else 12.sp,
        modifier = Modifier.padding(horizontal = 6.dp)
    )
}

@Composable
private fun StatusChip(status: String) {
    val label = when (status.uppercase()) {
        "SCHEDULED", "TIMED" -> "Scheduled"
        "IN_PLAY", "LIVE" -> "In play"
        "PAUSED" -> "Paused"
        "FINISHED" -> "Finished"
        "POSTPONED" -> "Postponed"
        "SUSPENDED" -> "Suspended"
        "CANCELLED", "CANCELED" -> "Cancelled"
        else -> status.ifBlank { "Scheduled" }
    }
    Surface(color = PaleOrangePanel, shape = RoundedCornerShape(50)) {
        Text(
            text = label,
            color = MatchRedOrange,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
