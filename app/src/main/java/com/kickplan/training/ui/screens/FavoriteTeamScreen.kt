package com.kickplan.training.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.ui.components.DarkSectionStrip
import com.kickplan.training.ui.components.MatchDayHeader
import com.kickplan.training.ui.theme.ErrorRed
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.StatusGreen
import com.kickplan.training.ui.theme.WhiteText

@Composable
fun FavoriteTeamScreen(
    favoriteTeam: String,
    onSave: (String) -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit
) {
    var team by remember { mutableStateOf(favoriteTeam) }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        MatchDayHeader(
            title = "Favorite Team",
            subtitle = "Saved locally on this device",
            onBack = onBack
        )

        Column(Modifier.padding(16.dp)) {
            DarkSectionStrip("Your Team")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = team,
                onValueChange = { team = it.take(60); note = "" },
                label = { Text("Favorite team name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter any team name you like. This is only used to highlight matches and show " +
                    "your team on the Today screen. No official club logos or branding are used.",
                color = SecondaryGrayText,
                fontSize = 12.sp
            )

            if (note.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(note, color = StatusGreen, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = { onSave(team); note = "Favorite team saved." },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchRedOrange, contentColor = WhiteText)
            ) {
                Text("Save", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { team = ""; onClear(); note = "Favorite team cleared." },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
            ) {
                Text("Clear Favorite Team", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
