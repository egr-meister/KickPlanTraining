package com.kickplan.training.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Single light Material 3 theme built around the red match-day palette.
 * No dynamic color / no dark theme -- keeps the energetic identity consistent
 * and the implementation simple and stable.
 */
private val KickPlanColorScheme = lightColorScheme(
    primary = MatchRedOrange,
    onPrimary = WhiteText,
    primaryContainer = PaleOrangePanel,
    onPrimaryContainer = DeepRedOrange,
    secondary = BrightOrangeAccent,
    onSecondary = WhiteText,
    tertiary = TeamBadgeBlue,
    onTertiary = WhiteText,
    background = LightAppBackground,
    onBackground = DeepNavyText,
    surface = WhiteCard,
    onSurface = CardDarkText,
    surfaceVariant = SoftGraySection,
    onSurfaceVariant = SecondaryGrayText,
    outline = MutedLabelGray,
    error = ErrorRed,
    onError = WhiteText
)

@Composable
fun KickPlanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KickPlanColorScheme,
        typography = AppTypography,
        content = content
    )
}
