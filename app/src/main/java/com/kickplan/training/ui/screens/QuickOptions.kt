package com.kickplan.training.ui.screens

import com.kickplan.training.data.model.Difficulty
import com.kickplan.training.data.model.TrainingType

/** Default quick-add football drills shown on the Today screen. */
data class QuickOption(
    val type: TrainingType,
    val minutes: Int,
    val difficulty: Difficulty
) {
    val label: String get() = "${type.label} · ${minutes} min · ${difficulty.label}"
}

val defaultQuickOptions: List<QuickOption> = listOf(
    QuickOption(TrainingType.Passing, 30, Difficulty.Medium),
    QuickOption(TrainingType.Shooting, 30, Difficulty.Medium),
    QuickOption(TrainingType.Running, 20, Difficulty.Hard),
    QuickOption(TrainingType.Defense, 30, Difficulty.Medium),
    QuickOption(TrainingType.Recovery, 15, Difficulty.Easy)
)
