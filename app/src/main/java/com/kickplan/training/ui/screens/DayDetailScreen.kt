package com.kickplan.training.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.data.model.AppData
import com.kickplan.training.ui.components.DarkSectionStrip
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.components.WhiteCard
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.ErrorRed
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.PaleOrangePanel
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.WhiteText
import com.kickplan.training.util.DateUtils
import com.kickplan.training.util.Stats

@Composable
fun DayDetailScreen(
    date: String,
    appData: AppData,
    onBack: () -> Unit,
    onEditSession: (String) -> Unit,
    onDeleteSession: (String) -> Unit,
    onResetDay: () -> Unit
) {
    val summary = Stats.daySummary(appData.sessions, date)
    val sessions = Stats.sessionsForDate(appData.sessions, date)
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(
            title = DateUtils.weekdayName(date).ifBlank { "Day" },
            subtitle = DateUtils.prettyDate(date),
            onBack = onBack
        )

        Column(Modifier.padding(16.dp)) {
            WhiteCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Stat("${summary.totalMinutes}", "minutes")
                    Stat("${summary.sessionCount}", "sessions")
                    Stat(summary.topType, "top focus")
                }
            }

            Spacer(Modifier.height(16.dp))
            DarkSectionStrip("Sessions", trailing = "${sessions.size}")
            Spacer(Modifier.height(10.dp))

            if (sessions.isEmpty()) {
                WhiteCard {
                    Text(
                        "No training sessions for this day.",
                        color = SecondaryGrayText,
                        fontSize = 14.sp
                    )
                }
            } else {
                sessions.forEach { session ->
                    WhiteCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(PaleOrangePanel, RoundedCornerShape(12.dp))
                                    .clickable { onEditSession(session.id) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    session.displayType.take(1).uppercase(),
                                    color = MatchRedOrange,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onEditSession(session.id) }
                            ) {
                                Text(session.displayType, color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    "${session.time.ifBlank { "--:--" }} · ${session.durationMinutes} min · ${session.difficulty.label} · ${session.intensity.label}",
                                    color = SecondaryGrayText,
                                    fontSize = 12.sp
                                )
                                if (session.notes.isNotBlank()) {
                                    Text(session.notes, color = SecondaryGrayText, fontSize = 12.sp)
                                }
                            }
                            IconButton(onClick = { onDeleteSession(session.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete session", tint = ErrorRed)
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
            DarkSectionStrip("Breakdown")
            Spacer(Modifier.height(10.dp))
            WhiteCard {
                Column {
                    Text("By type", color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    if (summary.typeBreakdown.isEmpty()) {
                        Text("-", color = SecondaryGrayText, fontSize = 13.sp)
                    } else {
                        summary.typeBreakdown.forEach { (type, count) ->
                            Text("$type: $count", color = SecondaryGrayText, fontSize = 13.sp)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text("By difficulty", color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    if (summary.difficultyBreakdown.isEmpty()) {
                        Text("-", color = SecondaryGrayText, fontSize = 13.sp)
                    } else {
                        summary.difficultyBreakdown.forEach { (d, count) ->
                            Text("${d.label}: $count", color = SecondaryGrayText, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = sessions.isNotEmpty(),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Text("Reset This Day", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset this day?") },
            text = { Text("This will remove all training sessions for the selected day.") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    onResetDay()
                }) { Text("Reset", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = MatchRedOrange, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Text(label, color = SecondaryGrayText, fontSize = 12.sp)
    }
}
