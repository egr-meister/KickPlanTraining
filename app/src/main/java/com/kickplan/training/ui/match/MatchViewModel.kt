package com.kickplan.training.ui.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kickplan.training.data.DemoData
import com.kickplan.training.data.local.AppRepository
import com.kickplan.training.data.model.MatchSource
import com.kickplan.training.data.model.NormalizedMatch
import com.kickplan.training.data.remote.FootballDataRepository
import com.kickplan.training.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Immutable UI state for the Match Schedule screen. */
data class MatchUiState(
    val isLoading: Boolean = false,
    val matches: List<NormalizedMatch> = emptyList(),
    val source: MatchSource = MatchSource.Demo,
    val lastUpdated: String = "",
    val message: String = "",
    val loadedOnce: Boolean = false
) {
    val isEmpty: Boolean get() = matches.isEmpty()

    val sourceLabel: String
        get() = when (source) {
            MatchSource.Api -> "Live data"
            MatchSource.Cache -> "Cached data"
            MatchSource.Demo -> "Demo data"
        }
}

class MatchViewModel(
    private val appRepository: AppRepository,
    private val footballRepository: FootballDataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    /** On first open: show cached data if present, otherwise fetch/demo. */
    fun loadInitialIfNeeded() {
        if (_uiState.value.loadedOnce) return
        viewModelScope.launch {
            val data = appRepository.appData.first()
            val cache = data.matchScheduleCache
            if (cache.cachedMatches.isNotEmpty()) {
                _uiState.value = MatchUiState(
                    isLoading = false,
                    matches = applyCompetitionFilter(
                        cache.cachedMatches.map { it.copy(source = MatchSource.Cache) },
                        data.settings.matchSchedule.competitionCode
                    ),
                    source = MatchSource.Cache,
                    lastUpdated = cache.lastUpdatedAt,
                    message = if (cache.lastError.isNotBlank()) cache.lastError else "",
                    loadedOnce = true
                )
            } else {
                _uiState.value = _uiState.value.copy(loadedOnce = true)
                refresh()
            }
        }
    }

    /** Manual refresh (user action only -- no background/auto polling). */
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val data = appRepository.appData.first()
            val settings = data.settings.matchSchedule
            val cached = data.matchScheduleCache.cachedMatches

            // Respect the "use demo data" preference and the API-enabled toggle.
            if (settings.useDemoData || !settings.apiEnabled) {
                _uiState.value = MatchUiState(
                    isLoading = false,
                    matches = applyCompetitionFilter(DemoData.demoMatches(), settings.competitionCode),
                    source = MatchSource.Demo,
                    lastUpdated = DateUtils.nowTimestamp(),
                    message = if (settings.useDemoData)
                        "Showing demo matches (demo data is enabled in settings)."
                    else
                        "Showing demo matches (Match Schedule API is disabled in settings).",
                    loadedOnce = true
                )
                return@launch
            }

            val result = footballRepository.fetchMatches(
                dateFrom = settings.dateFrom,
                dateTo = settings.dateTo
                // Competition is filtered locally for stability across API plans.
            )

            when {
                result.usedDemoData -> {
                    _uiState.value = MatchUiState(
                        isLoading = false,
                        matches = applyCompetitionFilter(result.matches, settings.competitionCode),
                        source = MatchSource.Demo,
                        lastUpdated = DateUtils.nowTimestamp(),
                        message = result.error,
                        loadedOnce = true
                    )
                }
                result.ok -> {
                    // Persist normalized matches to local cache.
                    appRepository.saveMatchCache(result.matches)
                    _uiState.value = MatchUiState(
                        isLoading = false,
                        matches = applyCompetitionFilter(result.matches, settings.competitionCode),
                        source = MatchSource.Api,
                        lastUpdated = DateUtils.nowTimestamp(),
                        message = if (result.matches.isEmpty())
                            "No matches were returned for the current filters." else "",
                        loadedOnce = true
                    )
                }
                else -> {
                    // API failed. Save the error, then fall back to cache or demo.
                    appRepository.setLastError(result.error)
                    if (cached.isNotEmpty()) {
                        _uiState.value = MatchUiState(
                            isLoading = false,
                            matches = applyCompetitionFilter(
                                cached.map { it.copy(source = MatchSource.Cache) },
                                settings.competitionCode
                            ),
                            source = MatchSource.Cache,
                            lastUpdated = data.matchScheduleCache.lastUpdatedAt,
                            message = "${result.error} Showing cached matches.",
                            loadedOnce = true
                        )
                    } else {
                        _uiState.value = MatchUiState(
                            isLoading = false,
                            matches = applyCompetitionFilter(DemoData.demoMatches(), settings.competitionCode),
                            source = MatchSource.Demo,
                            lastUpdated = DateUtils.nowTimestamp(),
                            message = "${result.error} Showing demo matches.",
                            loadedOnce = true
                        )
                    }
                }
            }
        }
    }

    private fun applyCompetitionFilter(
        matches: List<NormalizedMatch>,
        competitionCode: String
    ): List<NormalizedMatch> {
        if (competitionCode.isBlank()) return matches
        val filtered = matches.filter {
            it.competitionCode.equals(competitionCode.trim(), ignoreCase = true)
        }
        // If the filter removes everything, show all rather than an empty screen.
        return filtered.ifEmpty { matches }
    }
}
