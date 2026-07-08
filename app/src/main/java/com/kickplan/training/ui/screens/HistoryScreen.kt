package com.kickplan.training.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.data.model.AppData
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.components.WhiteCard
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.util.DateUtils
import com.kickplan.training.util.Stats

@Composable
fun HistoryScreen(
    appData: AppData,
    onBack: () -> Unit,
    onOpenDay: (String) -> Unit
) {
    val dates = Stats.historyDates(appData.sessions)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(
            title = "Training History",
            subtitle = "Your past training days",
            onBack = onBack
        )

        Column(Modifier.padding(16.dp)) {
            if (dates.isEmpty()) {
                WhiteCard {
                    Column {
                        Text("No training history yet.", color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Log a session to start building your timeline.", color = SecondaryGrayText, fontSize = 13.sp)
                    }
                }
            } else {
                dates.forEach { date ->
                    val summary = Stats.daySummary(appData.sessions, date)
                    WhiteCard(modifier = Modifier.clickable { onOpenDay(date) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = DateUtils.weekdayName(date).ifBlank { date },
                                    color = CardDarkText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(text = DateUtils.prettyDate(date), color = SecondaryGrayText, fontSize = 12.sp)
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "${summary.totalMinutes} min · ${summary.sessionCount} sessions · Top: ${summary.topType}",
                                    color = MatchRedOrange,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Open day",
                                tint = SecondaryGrayText
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
