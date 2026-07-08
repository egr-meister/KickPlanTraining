package com.kickplan.training.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kickplan.training.data.local.AppRepository
import com.kickplan.training.data.model.AppData
import com.kickplan.training.data.model.Difficulty
import com.kickplan.training.data.model.Intensity
import com.kickplan.training.data.model.MatchScheduleSettings
import com.kickplan.training.data.model.TrainingSession
import com.kickplan.training.data.model.TrainingType
import com.kickplan.training.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Owns all local training + settings state. The UI observes [appData]; every
 * mutation is delegated to the repository (DataStore) and flows back in.
 */
class AppViewModel(private val repository: AppRepository) : ViewModel() {

    val appData: StateFlow<AppData> = repository.appData
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppData()
        )

    // Becomes true once the first real emission from DataStore has arrived.
    // Used to keep the splash screen up until storage is ready.
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    // Snapshot captured at load time, used to pick the nav start destination.
    var onboardingCompletedAtStart: Boolean = false
        private set

    init {
        viewModelScope.launch {
            val data = repository.appData.first()
            onboardingCompletedAtStart = data.settings.onboardingCompleted
            _isLoaded.value = true
        }
    }

    // --- Sessions ---

    fun addSession(session: TrainingSession) = viewModelScope.launch {
        repository.addSession(session)
    }

    fun updateSession(session: TrainingSession) = viewModelScope.launch {
        repository.updateSession(session)
    }

    fun deleteSession(id: String) = viewModelScope.launch {
        repository.deleteSession(id)
    }

    fun resetDay(date: String) = viewModelScope.launch {
        repository.resetDay(date)
    }

    fun deleteAllSessions() = viewModelScope.launch {
        repository.deleteAllSessions()
    }

    /** Quick-add: create a session for today with the current time. */
    fun quickAdd(type: TrainingType, minutes: Int, difficulty: Difficulty) = viewModelScope.launch {
        repository.addSession(
            TrainingSession(
                date = DateUtils.today(),
                time = DateUtils.nowTime(),
                type = type,
                durationMinutes = minutes,
                difficulty = difficulty,
                intensity = Intensity.Normal,
                notes = ""
            )
        )
    }

    // --- Settings ---

    fun completeOnboarding() = viewModelScope.launch {
        repository.setOnboardingCompleted(true)
    }

    fun showOnboardingAgain() = viewModelScope.launch {
        repository.setOnboardingCompleted(false)
    }

    fun setCompactMode(enabled: Boolean) = viewModelScope.launch {
        repository.setCompactMode(enabled)
    }

    fun setFavoriteTeam(team: String) = viewModelScope.launch {
        repository.setFavoriteTeam(team.trim())
    }

    fun setMatchScheduleSettings(settings: MatchScheduleSettings) = viewModelScope.launch {
        repository.setMatchScheduleSettings(settings)
    }

    fun clearMatchCache() = viewModelScope.launch {
        repository.clearMatchCache()
    }

    fun resetAll() = viewModelScope.launch {
        repository.resetAll()
    }
}
