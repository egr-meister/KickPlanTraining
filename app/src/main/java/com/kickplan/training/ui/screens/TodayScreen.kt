package com.kickplan.training.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.data.model.AppData
import com.kickplan.training.data.model.Difficulty
import com.kickplan.training.data.model.NormalizedMatch
import com.kickplan.training.data.model.TrainingSession
import com.kickplan.training.data.model.TrainingType
import com.kickplan.training.ui.components.DarkSectionStrip
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.components.ProgressStrip
import com.kickplan.training.ui.components.StatCell
import com.kickplan.training.ui.components.TeamCircle
import com.kickplan.training.ui.components.WhiteCard
import com.kickplan.training.ui.theme.BrightOrangeAccent
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.DeepCharcoal
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.PaleOrangePanel
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.WhiteText
import com.kickplan.training.util.DateUtils
import com.kickplan.training.util.Stats

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodayScreen(
    appData: AppData,
    onQuickAdd: (TrainingType, Int, Difficulty) -> Unit,
    onAddTraining: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenWeekly: () -> Unit,
    onOpenMatches: () -> Unit,
    onOpenSettings: () -> Unit,
    onEditSession: (String) -> Unit
) {
    val today = DateUtils.today()
    val weekly = Stats.weeklyStats(appData.sessions)
    val todaySessions = Stats.sessionsForDate(appData.sessions, today)
    val nextMatch = pickNextMatch(appData.matchScheduleCache.cachedMatches)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(
            title = "KickPlan Training",
            subtitle = DateUtils.prettyDate(today),
            actionIcon = Icons.Filled.Settings,
            actionDescription = "Settings",
            onAction = onOpenSettings
        )

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Manual football training log",
                color = CardDarkText,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "Add sessions manually and track your week",
                color = SecondaryGrayText,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(16.dp))

            // ---- Weekly progress board (bold dark card) ----
            Surface(
                color = DeepCharcoal,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenWeekly)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "THIS WEEK",
                            color = WhiteText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (weekly.streak > 0) {
                            Text(
                                text = "🔥 ${weekly.streak}-day streak",
                                color = BrightOrangeAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        DarkStat("${weekly.totalSessions}", "sessions")
                        DarkStat("${weekly.totalMinutes}", "minutes")
                        DarkStat("${weekly.dailyAverageMinutes}", "avg min")
                    }
                    Spacer(Modifier.height(14.dp))
                    // Seven mini strips (Mon..Sun).
                    val maxMinutes = (weekly.perDayMinutes.maxOfOrNull { it.minutes } ?: 0).coerceAtLeast(1)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        weekly.perDayMinutes.forEach { day ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .background(WhiteText.copy(alpha = 0.12f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    val frac = day.minutes.toFloat() / maxMinutes
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height((40 * frac).dp.coerceAtLeast(if (day.minutes > 0) 6.dp else 0.dp))
                                            .background(BrightOrangeAccent, RoundedCornerShape(6.dp))
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = day.shortLabel.take(1),
                                    color = WhiteText.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Top focus: ${weekly.topFocus}  ·  Tap for full progress",
                        color = WhiteText.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // ---- Quick add drills ----
            DarkSectionStrip(text = "Quick Add Drill")
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                defaultQuickOptions.forEach { option ->
                    QuickChip(option.label) {
                        onQuickAdd(option.type, option.minutes, option.difficulty)
                    }
                }
                QuickChip(label = "+ Custom", filled = true, onClick = onAddTraining)
            }

            Spacer(Modifier.height(18.dp))

            // ---- Today's sessions ----
            DarkSectionStrip(text = "Today", trailing = "${todaySessions.size} sessions")
            Spacer(Modifier.height(10.dp))
            if (todaySessions.isEmpty()) {
                WhiteCard {
                    Column {
                        Text(
                            "No training logged today.",
                            color = CardDarkText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            "Add your first football session.",
                            color = SecondaryGrayText,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                todaySessions.forEach { session ->
                    SessionRowCard(session = session, onClick = { onEditSession(session.id) })
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(18.dp))

            // ---- Next match preview (supporting card) ----
            DarkSectionStrip(text = "Next Match")
            Spacer(Modifier.height(10.dp))
            WhiteCard(modifier = Modifier.clickable(onClick = onOpenMatches)) {
                if (nextMatch == null) {
                    Column {
                        Text(
                            "No cached matches yet.",
                            color = CardDarkText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            "Open Match Schedule to load football matches.",
                            color = SecondaryGrayText,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    MatchPreviewRow(nextMatch)
                }
            }

            Spacer(Modifier.height(10.dp))
            // Favorite team line.
            val fav = appData.settings.favoriteTeam
            Text(
                text = if (fav.isBlank()) "No favorite team selected." else "Favorite team: $fav",
                color = SecondaryGrayText,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(Modifier.height(18.dp))

            // ---- Shortcuts ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ShortcutTile(Icons.Filled.Add, "Add", Modifier.weight(1f), onAddTraining)
                ShortcutTile(Icons.Filled.CalendarMonth, "History", Modifier.weight(1f), onOpenHistory)
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ShortcutTile(Icons.Filled.Timeline, "Weekly", Modifier.weight(1f), onOpenWeekly)
                ShortcutTile(Icons.Filled.SportsSoccer, "Matches", Modifier.weight(1f), onOpenMatches)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun pickNextMatch(matches: List<NormalizedMatch>): NormalizedMatch? {
    if (matches.isEmpty()) return null
    val today = DateUtils.today()
    val upcoming = matches.filter { it.date.isNotBlank() && it.date >= today }.sortedBy { it.date }
    return upcoming.firstOrNull() ?: matches.sortedByDescending { it.date }.firstOrNull()
}

@Composable
private fun DarkStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = BrightOrangeAccent, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Text(label, color = WhiteText.copy(alpha = 0.7f), fontSize = 11.sp)
    }
}

@Composable
private fun QuickChip(label: String, filled: Boolean = false, onClick: () -> Unit) {
    Surface(
        color = if (filled) MatchRedOrange else PaleOrangePanel,
        shape = RoundedCornerShape(50),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (filled) WhiteText else MatchRedOrange,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun SessionRowCard(session: TrainingSession, onClick: () -> Unit) {
    WhiteCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(PaleOrangePanel, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = session.displayType.take(1).uppercase(),
                    color = MatchRedOrange,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(session.displayType, color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    "${session.time.ifBlank { "--:--" }} · ${session.difficulty.label} · ${session.intensity.label}",
                    color = SecondaryGrayText,
                    fontSize = 12.sp
                )
            }
            Text(
                "${session.durationMinutes} min",
                color = MatchRedOrange,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MatchPreviewRow(match: NormalizedMatch) {
    Column {
        Text(
            text = "${match.competitionName} · ${match.date} ${match.time}".trim(),
            color = SecondaryGrayText,
            fontSize = 11.sp
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TeamCircle(match.homeTeam, size = 34)
            Spacer(Modifier.width(8.dp))
            Text(match.homeTeam, color = CardDarkText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text("vs", color = SecondaryGrayText, fontSize = 12.sp)
            Text(match.awayTeam, color = CardDarkText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            TeamCircle(match.awayTeam, size = 34)
        }
    }
}

@Composable
private fun ShortcutTile(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label, tint = MatchRedOrange)
            Spacer(Modifier.width(8.dp))
            Text(label, color = CardDarkText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}
