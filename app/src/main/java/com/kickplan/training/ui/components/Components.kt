package com.kickplan.training.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kickplan.training.ui.theme.BrightOrangeAccent
import com.kickplan.training.ui.theme.CardDarkText
import com.kickplan.training.ui.theme.DarkMatchHeader
import com.kickplan.training.ui.theme.DeepCharcoal
import com.kickplan.training.ui.theme.DeepRedOrange
import com.kickplan.training.ui.theme.MatchRedOrange
import com.kickplan.training.ui.theme.SecondaryGrayText
import com.kickplan.training.ui.theme.TeamBadgeBlue
import com.kickplan.training.ui.theme.TeamBadgeGreen
import com.kickplan.training.ui.theme.TeamBadgeYellow
import com.kickplan.training.ui.theme.WhiteText

/**
 * Red match-day header used at the top of every screen. Strong red-orange
 * background with a rounded bottom edge, compact nav row, title and subtitle.
 */
@Composable
fun MatchDayHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    actionDescription: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(listOf(DeepRedOrange, MatchRedOrange)),
                shape = RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
            )
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = WhiteText
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                } else {
                    // Small football dot accent when there is no back button.
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(WhiteText, CircleShape)
                    )
                    Spacer(Modifier.width(10.dp))
                }
                Text(
                    text = title,
                    color = WhiteText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (actionIcon != null && onAction != null) {
                    IconButton(onClick = onAction) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = actionDescription,
                            tint = WhiteText
                        )
                    }
                }
            }
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = WhiteText.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }
        }
    }
}

/** Dark contrast strip used as a section header. */
@Composable
fun DarkSectionStrip(
    text: String,
    trailing: String? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DeepCharcoal,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text.uppercase(),
                color = WhiteText,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            if (!trailing.isNullOrBlank()) {
                Text(text = trailing, color = BrightOrangeAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

/** Rounded white card container. */
@Composable
fun WhiteCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(Modifier.padding(14.dp)) { content() }
    }
}

/** Circular team placeholder using initials. Never uses official logos. */
@Composable
fun TeamCircle(name: String, size: Int = 40) {
    val palette = listOf(TeamBadgeBlue, TeamBadgeGreen, MatchRedOrange, DarkMatchHeader, TeamBadgeYellow)
    val safeName = name.ifBlank { "?" }
    val color = palette[(safeName.hashCode() and 0x7fffffff) % palette.size]
    val initials = safeName.trim().split(" ", "-")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }
    val onColor = if (color == TeamBadgeYellow) CardDarkText else WhiteText
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initials, color = onColor, fontWeight = FontWeight.Bold, fontSize = (size / 2.8).sp)
    }
}

/** A compact key/value stat cell. */
@Composable
fun StatCell(value: String, label: String, valueColor: Color = MatchRedOrange, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = valueColor, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Text(
            text = label,
            color = SecondaryGrayText,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

/** Thin horizontal progress strip (0f..1f). */
@Composable
fun ProgressStrip(progress: Float, height: Int = 8, track: Color = Color(0xFFECEFF3), fill: Color = BrightOrangeAccent) {
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height.dp)
            .background(track, RoundedCornerShape(50))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .height(height.dp)
                .background(fill, RoundedCornerShape(50))
        )
    }
}

/** Standard vertical screen scaffold: header + scrollable body padding. */
@Composable
fun ScreenColumnPadding(): PaddingValues = PaddingValues(16.dp)

@Composable
fun VSpace(height: Int) = Spacer(Modifier.height(height.dp))

@Composable
fun RowStats(items: List<Pair<String, String>>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEach { (value, label) ->
            StatCell(value = value, label = label, modifier = Modifier.weight(1f))
        }
    }
}
