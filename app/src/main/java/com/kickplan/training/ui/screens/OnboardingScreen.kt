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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.ui.theme.DeepRedOrange
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.WhiteText

@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepRedOrange, MatchRedOrange)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(24.dp))

            // Simple football mark (white circle + dark pitch line) -- no logo.
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(WhiteText, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(30.dp)
                            .background(MatchRedOrange)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = "KickPlan Training",
                    color = WhiteText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Plan your football training week.",
                color = WhiteText,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(16.dp))

            OnboardBullet("Log sessions manually and track your progress.")
            OnboardBullet("See weekly progress across the last 7 days.")
            OnboardBullet("View football matches as an extra planning tool.")
            OnboardBullet("No account. No ads. No betting. No automatic tracking.")

            Spacer(Modifier.height(20.dp))

            DisclaimerBox(
                "KickPlan Training is a manual football training log. Training sessions are " +
                    "added by the user. The app does not track activity automatically, does not use " +
                    "sensors, does not connect to Google Fit or Health Connect, and does not provide " +
                    "medical or professional coaching advice."
            )
            Spacer(Modifier.height(12.dp))
            DisclaimerBox(
                "Match schedule data is provided by football-data.org. Availability, accuracy, " +
                    "competitions, and update frequency depend on the API provider and the current API plan."
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WhiteText,
                    contentColor = MatchRedOrange
                )
            ) {
                Text("Start Training Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WhiteText)
            ) {
                Text("Skip", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OnboardBullet(text: String) {
    Row(modifier = Modifier.padding(vertical = 5.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(7.dp)
                .background(WhiteText, CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Text(text = text, color = WhiteText, fontSize = 15.sp)
    }
}

@Composable
private fun DisclaimerBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(WhiteText.copy(alpha = 0.14f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(text = text, color = WhiteText, fontSize = 12.sp, lineHeight = 17.sp)
    }
}
