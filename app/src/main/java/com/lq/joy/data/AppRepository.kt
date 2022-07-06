package com.lq.joy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppRepository(private val context: Context) {

    companion object {
        val KEY_SEARCH_FILTER = stringSetPreferencesKey("search_filter")
    }

    suspend fun saveSearchFilter(names: Set<String>) {
        context.dataStore.edit {
            it[KEY_SEARCH_FILTER] = names
        }
    }

    fun getSearchFilter(): Flow<Set<String>?> {
        return context.dataStore.data.map {
            it[KEY_SEARCH_FILTER]
        }
    }
}