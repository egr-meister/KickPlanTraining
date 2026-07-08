package com.kickplan.training.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.data.model.AppData
import com.kickplan.training.ui.components.DarkSectionStrip
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.components.WhiteCard
import com.kickplan.training.ui.theme.BrightOrangeAccent
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.SoftGraySection
import com.kickplan.training.util.Stats

@Composable
fun WeeklyProgressScreen(
    appData: AppData,
    onBack: () -> Unit
) {
    val stats = Stats.weeklyStats(appData.sessions)
    val maxMinutes = (stats.perDayMinutes.maxOfOrNull { it.minutes } ?: 0).coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(
            title = "Weekly Progress",
            subtitle = "Last 7 days (week starts Monday)",
            onBack = onBack
        )

        Column(Modifier.padding(16.dp)) {

            // ---- Seven day bar chart (simple Compose, no chart library) ----
            WhiteCard {
                Column {
                    Text("Minutes per day", color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        stats.perDayMinutes.forEach { day ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Text(
                                    text = if (day.minutes > 0) "${day.minutes}" else "",
                                    color = MatchRedOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .background(SoftGraySection, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    val frac = day.minutes.toFloat() / maxMinutes
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(frac.coerceIn(0f, 1f))
                                            .background(BrightOrangeAccent, RoundedCornerShape(8.dp))
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(day.shortLabel, color = SecondaryGrayText, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            DarkSectionStrip("Summary")
            Spacer(Modifier.height(10.dp))

            SummaryRow("This week", "${stats.totalSessions} sessions")
            SummaryRow("Total time", "${stats.totalMinutes} min")
            SummaryRow("Daily average", "${stats.dailyAverageMinutes} min")
            SummaryRow("Best day", if (stats.bestDayMinutes > 0) "${stats.bestDay} (${stats.bestDayMinutes} min)" else "-")
            SummaryRow("Top focus", stats.topFocus)
            SummaryRow("Hard sessions", "${stats.hardSessions}")
            SummaryRow("Current streak", "${stats.streak} day(s)")

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    WhiteCard(modifier = Modifier.padding(bottom = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = SecondaryGrayText, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(value, color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
