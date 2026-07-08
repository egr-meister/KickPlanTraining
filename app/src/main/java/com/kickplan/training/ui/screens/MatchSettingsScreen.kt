package com.kickplan.training.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.kickplan.training.data.model.MatchScheduleSettings
import com.kickplan.training.ui.components.DarkSectionStrip
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.components.WhiteCard
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.ErrorRed
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.StatusGreen
import com.kickplan.training.ui.theme.WhiteText
import com.kickplan.training.util.DateUtils

@Composable
fun MatchSettingsScreen(
    settings: MatchScheduleSettings,
    onSave: (MatchScheduleSettings) -> Unit,
    onClearCache: () -> Unit,
    onBack: () -> Unit
) {
    var apiEnabled by remember { mutableStateOf(settings.apiEnabled) }
    var useDemo by remember { mutableStateOf(settings.useDemoData) }
    var dateFrom by remember { mutableStateOf(settings.dateFrom) }
    var dateTo by remember { mutableStateOf(settings.dateTo) }
    var competitionCode by remember { mutableStateOf(settings.competitionCode) }
    var errorText by remember { mutableStateOf("") }
    var savedNote by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(
            title = "Match Schedule Settings",
            subtitle = "Control how matches are loaded",
            onBack = onBack
        )

        Column(Modifier.padding(16.dp)) {

            DarkSectionStrip("Data Source")
            Spacer(Modifier.height(10.dp))
            WhiteCard {
                Column {
                    ToggleRow(
                        title = "API enabled",
                        subtitle = "Allow loading matches from football-data.org",
                        checked = apiEnabled,
                        onCheckedChange = { apiEnabled = it }
                    )
                    Spacer(Modifier.height(8.dp))
                    ToggleRow(
                        title = "Use demo data",
                        subtitle = "Always show built-in demo matches",
                        checked = useDemo,
                        onCheckedChange = { useDemo = it }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            DarkSectionStrip("Filters (optional)")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = dateFrom,
                onValueChange = { dateFrom = it; savedNote = "" },
                label = { Text("Date from (YYYY-MM-DD)") },
                singleLine = true,
                isError = dateFrom.isNotBlank() && !DateUtils.isValidDate(dateFrom),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = dateTo,
                onValueChange = { dateTo = it; savedNote = "" },
                label = { Text("Date to (YYYY-MM-DD)") },
                singleLine = true,
                isError = dateTo.isNotBlank() && !DateUtils.isValidDate(dateTo),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = competitionCode,
                onValueChange = { competitionCode = it; savedNote = "" },
                label = { Text("Competition code (e.g. PL, PD) - optional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Availability of competitions and date ranges depends on your API plan. Empty date " +
                    "fields load the default /matches list. The competition code is applied locally.",
                color = SecondaryGrayText,
                fontSize = 12.sp
            )

            if (errorText.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(errorText, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            if (savedNote.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(savedNote, color = StatusGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = {
                    if (dateFrom.isNotBlank() && !DateUtils.isValidDate(dateFrom)) {
                        errorText = "Date from must be empty or a valid YYYY-MM-DD date."; return@Button
                    }
                    if (dateTo.isNotBlank() && !DateUtils.isValidDate(dateTo)) {
                        errorText = "Date to must be empty or a valid YYYY-MM-DD date."; return@Button
                    }
                    errorText = ""
                    onSave(
                        MatchScheduleSettings(
                            apiEnabled = apiEnabled,
                            useDemoData = useDemo,
                            dateFrom = dateFrom.trim(),
                            dateTo = dateTo.trim(),
                            competitionCode = competitionCode.trim()
                        )
                    )
                    savedNote = "Settings saved. Refresh the Match Schedule to apply."
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchRedOrange, contentColor = WhiteText)
            ) {
                Text("Save Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { onClearCache(); savedNote = "Match cache cleared." },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Text("Clear Match Cache", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = CardDarkText, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = SecondaryGrayText, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = MatchRedOrange)
        )
    }
}
