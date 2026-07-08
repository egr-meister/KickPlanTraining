package com.kickplan.training.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kickplan.training.data.model.AppData
import com.kickplan.training.data.model.MatchScheduleCache
import com.kickplan.training.data.model.MatchScheduleSettings
import com.kickplan.training.data.model.NormalizedMatch
import com.kickplan.training.data.model.Settings
import com.kickplan.training.data.model.TrainingSession
import com.kickplan.training.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.UUID

// Single DataStore for the whole app.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "kickplan_app_data"
)

/**
 * Local repository backed by DataStore Preferences. The full AppData object is
 * stored as one JSON string. All reads merge with defaults and fall back to a
 * fresh AppData() on any corruption, so the app never crashes on bad data.
 */
class AppRepository(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
    }

    private val appDataKey = stringPreferencesKey("app_data_json")

    /** Observable app data. Emits a safe default on empty or corrupted storage. */
    val appData: Flow<AppData> = context.dataStore.data
        .catch {
            // IOException reading preferences -> emit empty prefs.
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { prefs ->
            val raw = prefs[appDataKey]
            decodeSafely(raw)
        }

    private fun decodeSafely(raw: String?): AppData {
        if (raw.isNullOrBlank()) return AppData()
        return try {
            json.decodeFromString(AppData.serializer(), raw)
        } catch (e: Exception) {
            // Corrupted JSON -> safe fallback.
            AppData()
        }
    }

    private suspend fun update(transform: (AppData) -> AppData) {
        context.dataStore.edit { prefs ->
            val current = decodeSafely(prefs[appDataKey])
            val next = transform(current)
            prefs[appDataKey] = json.encodeToString(AppData.serializer(), next)
        }
    }

    // -----------------------------------------------------------------------
    // Training sessions
    // -----------------------------------------------------------------------

    /** Adds a new session (assigns id + timestamps if missing). */
    suspend fun addSession(session: TrainingSession) = update { data ->
        val now = DateUtils.nowTimestamp()
        val withMeta = session.copy(
            id = session.id.ifBlank { UUID.randomUUID().toString() },
            createdAt = session.createdAt.ifBlank { now },
            updatedAt = now
        )
        data.copy(sessions = data.sessions + withMeta)
    }

    /** Updates an existing session by id; adds it if not found. */
    suspend fun updateSession(session: TrainingSession) = update { data ->
        val now = DateUtils.nowTimestamp()
        val exists = data.sessions.any { it.id == session.id }
        val newList = if (exists) {
            data.sessions.map { if (it.id == session.id) session.copy(updatedAt = now) else it }
        } else {
            data.sessions + session.copy(
                id = session.id.ifBlank { UUID.randomUUID().toString() },
                createdAt = session.createdAt.ifBlank { now },
                updatedAt = now
            )
        }
        data.copy(sessions = newList)
    }

    suspend fun deleteSession(id: String) = update { data ->
        data.copy(sessions = data.sessions.filterNot { it.id == id })
    }

    /** Removes all sessions on a given date (reset day). */
    suspend fun resetDay(date: String) = update { data ->
        data.copy(sessions = data.sessions.filterNot { it.date == date })
    }

    suspend fun deleteAllSessions() = update { data ->
        data.copy(sessions = emptyList())
    }

    // -----------------------------------------------------------------------
    // Settings
    // -----------------------------------------------------------------------

    suspend fun setOnboardingCompleted(completed: Boolean) = update { data ->
        data.copy(settings = data.settings.copy(onboardingCompleted = completed))
    }

    suspend fun setCompactMode(enabled: Boolean) = update { data ->
        data.copy(settings = data.settings.copy(compactMode = enabled))
    }

    suspend fun setFavoriteTeam(team: String) = update { data ->
        data.copy(settings = data.settings.copy(favoriteTeam = team))
    }

    suspend fun setMatchScheduleSettings(settings: MatchScheduleSettings) = update { data ->
        data.copy(settings = data.settings.copy(matchSchedule = settings))
    }

    // -----------------------------------------------------------------------
    // Match cache
    // -----------------------------------------------------------------------

    suspend fun saveMatchCache(matches: List<NormalizedMatch>, error: String = "") = update { data ->
        data.copy(
            matchScheduleCache = MatchScheduleCache(
                cachedMatches = matches,
                lastUpdatedAt = DateUtils.nowTimestamp(),
                lastError = error
            )
        )
    }

    suspend fun setLastError(error: String) = update { data ->
        data.copy(matchScheduleCache = data.matchScheduleCache.copy(lastError = error))
    }

    suspend fun clearMatchCache() = update { data ->
        data.copy(matchScheduleCache = MatchScheduleCache())
    }

    // -----------------------------------------------------------------------
    // Full reset
    // -----------------------------------------------------------------------

    suspend fun resetAll() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}
