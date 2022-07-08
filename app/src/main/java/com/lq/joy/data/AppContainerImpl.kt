package com.lq.joy.data

import android.content.Context
import androidx.room.Room
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.netfix.NaifeiRepository
import com.lq.joy.data.sakura.ISakuraRepository
import com.lq.joy.data.sakura.SakuraRepository
import com.lq.joy.db.AppDatabase


interface AppContainer {
    val sakuraRepository: ISakuraRepository
    val naifeiRepository: INaifeiRepository
    val appRepository: AppRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    private val db = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "joy_database"
    ).build()


    override val sakuraRepository: ISakuraRepository by lazy {
        SakuraRepository()
//        FakeSakuraRepository(applicationContext)

    }
    override val naifeiRepository: INaifeiRepository by lazy {
        NaifeiRepository()
//        FakeNaifeiRepository()
    }
    override val appRepository: AppRepository by lazy {
        AppRepository(applicationContext, db)
    }


}