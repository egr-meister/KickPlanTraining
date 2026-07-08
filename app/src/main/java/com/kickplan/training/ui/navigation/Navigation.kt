package com.kickplan.training.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kickplan.training.ui.AppViewModel
import com.kickplan.training.ui.match.MatchViewModel
import com.kickplan.training.ui.screens.AddEditTrainingScreen
import com.kickplan.training.ui.screens.DayDetailScreen
import com.kickplan.training.ui.screens.FavoriteTeamScreen
import com.kickplan.training.ui.screens.HistoryScreen
import com.kickplan.training.ui.screens.MatchScheduleScreen
import com.kickplan.training.ui.screens.MatchSettingsScreen
import com.kickplan.training.ui.screens.OnboardingScreen
import com.kickplan.training.ui.screens.SettingsScreen
import com.kickplan.training.ui.screens.TodayScreen
import com.kickplan.training.ui.screens.WeeklyProgressScreen

/** Central route definitions. */
object Routes {
    const val ONBOARDING = "onboarding"
    const val TODAY = "today"
    const val ADD_EDIT = "add_edit"
    const val DAY_DETAIL = "day_detail"
    const val HISTORY = "history"
    const val WEEKLY = "weekly"
    const val MATCH = "match"
    const val MATCH_SETTINGS = "match_settings"
    const val FAVORITE_TEAM = "favorite_team"
    const val SETTINGS = "settings"

    const val ARG_SESSION_ID = "sessionId"
    const val ARG_DATE = "date"

    /** Add screen (no id) or edit screen (with id). */
    fun addEdit(sessionId: String? = null): String =
        if (sessionId.isNullOrBlank()) ADD_EDIT else "$ADD_EDIT?$ARG_SESSION_ID=$sessionId"

    fun dayDetail(date: String): String = "$DAY_DETAIL/$date"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appViewModel: AppViewModel,
    matchViewModel: MatchViewModel,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    appViewModel.completeOnboarding()
                    navController.navigateTopLevel(Routes.TODAY, popTo = Routes.ONBOARDING)
                }
            )
        }

        composable(Routes.TODAY) {
            val data by appViewModel.appData.collectAsState()
            TodayScreen(
                appData = data,
                onQuickAdd = appViewModel::quickAdd,
                onAddTraining = { navController.navigate(Routes.addEdit()) },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onOpenWeekly = { navController.navigate(Routes.WEEKLY) },
                onOpenMatches = { navController.navigate(Routes.MATCH) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onEditSession = { id -> navController.navigate(Routes.addEdit(id)) }
            )
        }

        composable(
            route = "${Routes.ADD_EDIT}?${Routes.ARG_SESSION_ID}={${Routes.ARG_SESSION_ID}}",
            arguments = listOf(
                navArgument(Routes.ARG_SESSION_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            val data by appViewModel.appData.collectAsState()
            val sessionId = entry.arguments?.getString(Routes.ARG_SESSION_ID)
            AddEditTrainingScreen(
                existingSession = data.sessions.firstOrNull { it.id == sessionId },
                onSave = { session ->
                    if (data.sessions.any { it.id == session.id }) {
                        appViewModel.updateSession(session)
                    } else {
                        appViewModel.addSession(session)
                    }
                    navController.popBackStack()
                },
                onDelete = { id ->
                    appViewModel.deleteSession(id)
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.DAY_DETAIL}/{${Routes.ARG_DATE}}",
            arguments = listOf(navArgument(Routes.ARG_DATE) { type = NavType.StringType })
        ) { entry ->
            val data by appViewModel.appData.collectAsState()
            val date = entry.arguments?.getString(Routes.ARG_DATE).orEmpty()
            DayDetailScreen(
                date = date,
                appData = data,
                onBack = { navController.popBackStack() },
                onEditSession = { id -> navController.navigate(Routes.addEdit(id)) },
                onDeleteSession = appViewModel::deleteSession,
                onResetDay = { appViewModel.resetDay(date) }
            )
        }

        composable(Routes.HISTORY) {
            val data by appViewModel.appData.collectAsState()
            HistoryScreen(
                appData = data,
                onBack = { navController.popBackStack() },
                onOpenDay = { date -> navController.navigate(Routes.dayDetail(date)) }
            )
        }

        composable(Routes.WEEKLY) {
            val data by appViewModel.appData.collectAsState()
            WeeklyProgressScreen(
                appData = data,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.MATCH) {
            val data by appViewModel.appData.collectAsState()
            val state by matchViewModel.uiState.collectAsState()
            MatchScheduleScreen(
                state = state,
                favoriteTeam = data.settings.favoriteTeam,
                onLoad = matchViewModel::loadInitialIfNeeded,
                onRefresh = matchViewModel::refresh,
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(Routes.MATCH_SETTINGS) }
            )
        }

        composable(Routes.MATCH_SETTINGS) {
            val data by appViewModel.appData.collectAsState()
            MatchSettingsScreen(
                settings = data.settings.matchSchedule,
                onSave = appViewModel::setMatchScheduleSettings,
                onClearCache = appViewModel::clearMatchCache,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FAVORITE_TEAM) {
            val data by appViewModel.appData.collectAsState()
            FavoriteTeamScreen(
                favoriteTeam = data.settings.favoriteTeam,
                onSave = appViewModel::setFavoriteTeam,
                onClear = { appViewModel.setFavoriteTeam("") },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            val data by appViewModel.appData.collectAsState()
            SettingsScreen(
                appData = data,
                onBack = { navController.popBackStack() },
                onOpenFavoriteTeam = { navController.navigate(Routes.FAVORITE_TEAM) },
                onOpenMatchSettings = { navController.navigate(Routes.MATCH_SETTINGS) },
                onToggleCompact = appViewModel::setCompactMode,
                onShowOnboarding = {
                    appViewModel.showOnboardingAgain()
                    navController.navigateTopLevel(Routes.ONBOARDING, popTo = Routes.TODAY)
                },
                onClearMatchCache = appViewModel::clearMatchCache,
                onDeleteAllSessions = appViewModel::deleteAllSessions,
                onResetAll = {
                    appViewModel.resetAll()
                    navController.navigateTopLevel(Routes.ONBOARDING, popTo = Routes.TODAY)
                }
            )
        }
    }
}

/** Navigate and clear the given route from the back stack. */
private fun NavHostController.navigateTopLevel(route: String, popTo: String) {
    navigate(route) {
        popUpTo(popTo) { inclusive = true }
        launchSingleTop = true
    }
}
