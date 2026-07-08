package com.kickplan.training.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.data.model.Difficulty
import com.kickplan.training.data.model.Intensity
import com.kickplan.training.data.model.TrainingSession
import com.kickplan.training.data.model.TrainingType
import com.kickplan.training.ui.components.DarkSectionStrip
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.theme.ErrorRed
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.PaleOrangePanel
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.WhiteText
import com.kickplan.training.util.DateUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditTrainingScreen(
    existingSession: TrainingSession?,
    onSave: (TrainingSession) -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit
) {
    val isEdit = existingSession != null

    var date by remember { mutableStateOf(existingSession?.date?.ifBlank { DateUtils.today() } ?: DateUtils.today()) }
    var time by remember { mutableStateOf(existingSession?.time?.ifBlank { DateUtils.nowTime() } ?: DateUtils.nowTime()) }
    var type by remember { mutableStateOf(existingSession?.type ?: TrainingType.Passing) }
    var customType by remember { mutableStateOf(existingSession?.customType ?: "") }
    var duration by remember { mutableStateOf((existingSession?.durationMinutes ?: 30).toString()) }
    var difficulty by remember { mutableStateOf(existingSession?.difficulty ?: Difficulty.Medium) }
    var intensity by remember { mutableStateOf(existingSession?.intensity ?: Intensity.Normal) }
    var notes by remember { mutableStateOf(existingSession?.notes ?: "") }

    var errorText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        MatchDayHeader(
            title = if (isEdit) "Edit Session" else "Add Training",
            subtitle = "Manual football training entry",
            onBack = onBack
        )

        Column(Modifier.padding(16.dp)) {

            DarkSectionStrip("When")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY-MM-DD)") },
                singleLine = true,
                isError = !DateUtils.isValidDate(date),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Time (HH:mm)") },
                singleLine = true,
                isError = !DateUtils.isValidTime(time),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))
            DarkSectionStrip("Training Type")
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TrainingType.entries.forEach { t ->
                    FilterChip(
                        selected = type == t,
                        onClick = { type = t },
                        label = { Text(t.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MatchRedOrange,
                            selectedLabelColor = WhiteText
                        )
                    )
                }
            }
            if (type == TrainingType.Custom) {
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = customType,
                    onValueChange = { customType = it },
                    label = { Text("Custom type name") },
                    singleLine = true,
                    isError = type == TrainingType.Custom && customType.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(18.dp))
            DarkSectionStrip("Duration")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = duration,
                onValueChange = { new -> duration = new.filter { it.isDigit() }.take(3) },
                label = { Text("Minutes (1-300)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = !isDurationValid(duration),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))
            DarkSectionStrip("Difficulty")
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Difficulty.entries.forEach { d ->
                    FilterChip(
                        selected = difficulty == d,
                        onClick = { difficulty = d },
                        label = { Text(d.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MatchRedOrange,
                            selectedLabelColor = WhiteText
                        )
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            DarkSectionStrip("Intensity")
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Intensity.entries.forEach { i ->
                    FilterChip(
                        selected = intensity == i,
                        onClick = { intensity = i },
                        label = { Text(i.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MatchRedOrange,
                            selectedLabelColor = WhiteText
                        )
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            DarkSectionStrip("Notes")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it.take(500) },
                label = { Text("Notes (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )

            if (errorText.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(text = errorText, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    val validation = validate(date, time, type, customType, duration)
                    if (validation != null) {
                        errorText = validation
                        return@Button
                    }
                    errorText = ""
                    onSave(
                        TrainingSession(
                            id = existingSession?.id ?: "",
                            date = date.trim(),
                            time = time.trim(),
                            type = type,
                            customType = if (type == TrainingType.Custom) customType.trim() else "",
                            durationMinutes = duration.toIntOrNull() ?: 0,
                            difficulty = difficulty,
                            intensity = intensity,
                            notes = notes.trim(),
                            createdAt = existingSession?.createdAt ?: "",
                            updatedAt = existingSession?.updatedAt ?: ""
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchRedOrange, contentColor = WhiteText)
            ) {
                Text("Save Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (isEdit) {
                Spacer(Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onDelete(existingSession!!.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text("Delete Session", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

private fun isDurationValid(value: String): Boolean {
    val n = value.toIntOrNull() ?: return false
    return n in 1..300
}

private fun validate(
    date: String,
    time: String,
    type: TrainingType,
    customType: String,
    duration: String
): String? {
    if (!DateUtils.isValidDate(date)) return "Please enter a valid date as YYYY-MM-DD."
    if (!DateUtils.isValidTime(time)) return "Please enter a valid time as HH:mm."
    if (type == TrainingType.Custom && customType.isBlank()) return "Please enter a name for the custom training type."
    val n = duration.toIntOrNull()
    if (n == null || n <= 0) return "Duration must be greater than 0 minutes."
    if (n > 300) return "Duration must not exceed 300 minutes per session."
    return null
}
