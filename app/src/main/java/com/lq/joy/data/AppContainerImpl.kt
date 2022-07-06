package com.lq.joy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.lq.joy.data.netfix.FakeNaifeiRepository
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.netfix.NaifeiRepository
import com.lq.joy.data.sakura.FakeSakuraRepository
import com.lq.joy.data.sakura.ISakuraRepository
import com.lq.joy.data.sakura.SakuraRepository


interface AppContainer {
    val sakuraRepository: ISakuraRepository
    val naifeiRepository: INaifeiRepository
    val appRepository:AppRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val sakuraRepository: ISakuraRepository by lazy {
        SakuraRepository()
//        FakeSakuraRepository(applicationContext)

    }
    override val naifeiRepository: INaifeiRepository by lazy {
        NaifeiRepository()
//        FakeNaifeiRepository()
    }
    override val appRepository: AppRepository by lazy {
        AppRepository(applicationContext)
    }


}