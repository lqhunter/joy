package com.lq.joy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lq.joy.data.netfix.NaifeiSearchSource
import com.lq.joy.data.ui.VideoSearchBean
import com.lq.joy.db.AppDatabase
import com.lq.joy.db.Favourite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppRepository(private val context: Context, private val db: AppDatabase) {

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

    fun getFavouriteList(): Flow<PagingData<Favourite>> {
        return Pager(PagingConfig(10)) {
            db.historyDao().getFavouriteList()
        }.flow
    }

    fun isFavourite(type: SourceType, uniqueTag: String):Flow<Boolean> {
        return db.historyDao().getFavourite(type, uniqueTag).map {
            null != it
        }
    }
}