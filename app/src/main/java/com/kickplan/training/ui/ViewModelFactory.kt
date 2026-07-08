package com.kickplan.training.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kickplan.training.data.local.AppRepository
import com.kickplan.training.data.remote.FootballDataRepository
import com.kickplan.training.ui.match.MatchViewModel

/**
 * Simple factory that builds the two app ViewModels with their repositories.
 * Uses the application context to avoid leaking an Activity.
 */
class AppViewModelFactory(context: Context) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext
    private val appRepository = AppRepository(appContext)
    private val footballRepository = FootballDataRepository()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppViewModel::class.java) ->
                AppViewModel(appRepository) as T
            modelClass.isAssignableFrom(MatchViewModel::class.java) ->
                MatchViewModel(appRepository, footballRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
