package com.kickplan.training.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.kickplan.training.BuildConfig
import com.kickplan.training.data.model.AppData
import com.kickplan.training.ui.components.DarkSectionStrip
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.components.WhiteCard
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.ErrorRed
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.StatusGreen

@Composable
fun SettingsScreen(
    appData: AppData,
    onBack: () -> Unit,
    onOpenFavoriteTeam: () -> Unit,
    onOpenMatchSettings: () -> Unit,
    onToggleCompact: (Boolean) -> Unit,
    onShowOnboarding: () -> Unit,
    onClearMatchCache: () -> Unit,
    onDeleteAllSessions: () -> Unit,
    onResetAll: () -> Unit
) {
    var showDeleteSessions by remember { mutableStateOf(false) }
    var showResetAll by remember { mutableStateOf(false) }

    val tokenConfigured = BuildConfig.FOOTBALL_DATA_API_TOKEN.isNotBlank() &&
        BuildConfig.FOOTBALL_DATA_API_TOKEN != "your_api_token_here"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(title = "Settings", subtitle = "KickPlan Training", onBack = onBack)

        Column(Modifier.padding(16.dp)) {

            DarkSectionStrip("Preferences")
            Spacer(Modifier.height(10.dp))
            NavRow("Favorite team", if (appData.settings.favoriteTeam.isBlank()) "Not set" else appData.settings.favoriteTeam, onOpenFavoriteTeam)
            Spacer(Modifier.height(8.dp))
            NavRow("Match Schedule settings", "Data source & filters", onOpenMatchSettings)
            Spacer(Modifier.height(8.dp))
            WhiteCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Compact mode", color = CardDarkText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text("Tighter spacing on cards", color = SecondaryGrayText, fontSize = 12.sp)
                    }
                    Switch(
                        checked = appData.settings.compactMode,
                        onCheckedChange = onToggleCompact,
                        colors = SwitchDefaults.colors(checkedTrackColor = MatchRedOrange)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            DarkSectionStrip("API Status")
            Spacer(Modifier.height(10.dp))
            WhiteCard {
                Column {
                    Row {
                        Text("Token: ", color = SecondaryGrayText, fontSize = 14.sp)
                        Text(
                            if (tokenConfigured) "Configured" else "Not configured (demo mode)",
                            color = if (tokenConfigured) StatusGreen else MatchRedOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("Base URL: ${BuildConfig.FOOTBALL_API_BASE_URL}", color = SecondaryGrayText, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            DarkSectionStrip("Data")
            Spacer(Modifier.height(10.dp))
            ActionRow("Show onboarding again", onShowOnboarding)
            Spacer(Modifier.height(8.dp))
            ActionRow("Clear match cache", onClearMatchCache)
            Spacer(Modifier.height(8.dp))
            ActionRow("Delete all training records", { showDeleteSessions = true }, danger = true)
            Spacer(Modifier.height(8.dp))
            ActionRow("Reset all local data", { showResetAll = true }, danger = true)

            Spacer(Modifier.height(16.dp))
            DarkSectionStrip("About")
            Spacer(Modifier.height(10.dp))
            WhiteCard {
                Column {
                    Text("KickPlan Training", color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Version ${BuildConfig.VERSION_NAME}", color = SecondaryGrayText, fontSize = 12.sp)
                    Text(
                        "Track your football training manually and view upcoming matches in one simple planner.",
                        color = SecondaryGrayText,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            InfoCard(
                "Manual tracking",
                "KickPlan Training is a manual football training log. Training sessions are added by " +
                    "the user. The app does not track activity automatically, does not use sensors, does not " +
                    "connect to Google Fit or Health Connect, and does not provide medical or professional " +
                    "coaching advice."
            )
            Spacer(Modifier.height(10.dp))
            InfoCard(
                "Match schedule",
                "Match schedule data is provided by football-data.org. Availability, accuracy, " +
                    "competitions, and update frequency depend on the API provider and the current API plan."
            )
            Spacer(Modifier.height(10.dp))
            InfoCard(
                "Privacy",
                "KickPlan Training stores training sessions, settings, favorite team, and cached match " +
                    "data on this device. The app uses internet only to load football match data from " +
                    "football-data.org. No account, no ads, no analytics, no payments, no Firebase, no location, " +
                    "no notifications, no sensors, no Google Fit, and no Health Connect."
            )

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDeleteSessions) {
        ConfirmDialog(
            title = "Delete all training records?",
            message = "This permanently removes every training session stored on this device. Settings and match cache are kept.",
            confirmLabel = "Delete",
            onConfirm = { showDeleteSessions = false; onDeleteAllSessions() },
            onDismiss = { showDeleteSessions = false }
        )
    }
    if (showResetAll) {
        ConfirmDialog(
            title = "Reset all local data?",
            message = "This clears all training sessions, settings, favorite team, and cached matches, and returns the app to first-launch state.",
            confirmLabel = "Reset",
            onConfirm = { showResetAll = false; onResetAll() },
            onDismiss = { showResetAll = false }
        )
    }
}

@Composable
private fun NavRow(title: String, subtitle: String, onClick: () -> Unit) {
    WhiteCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = CardDarkText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = SecondaryGrayText, fontSize = 12.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = SecondaryGrayText)
        }
    }
}

@Composable
private fun ActionRow(title: String, onClick: () -> Unit, danger: Boolean = false) {
    WhiteCard(modifier = Modifier.clickable(onClick = onClick)) {
        Text(
            title,
            color = if (danger) ErrorRed else CardDarkText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    WhiteCard {
        Column {
            Text(title, color = CardDarkText, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text(body, color = SecondaryGrayText, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel, color = ErrorRed) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
