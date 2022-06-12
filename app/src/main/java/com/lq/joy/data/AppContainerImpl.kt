package com.lq.joy.data

import android.content.Context
import com.lq.joy.data.netfix.FakeNaifeiRepository
import com.lq.joy.data.netfix.INaifeiRepository
import com.lq.joy.data.sakura.FakeSakuraRepository
import com.lq.joy.data.sakura.ISakuraRepository
import com.lq.joy.data.sakura.SakuraRepository


interface AppContainer {
    val sakuraRepository: ISakuraRepository
    val naifeiRepository: INaifeiRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val sakuraRepository: ISakuraRepository by lazy {
//        SakuraRepository()
        FakeSakuraRepository(applicationContext)

    }
    override val naifeiRepository: INaifeiRepository by lazy {
//        NaifeiRepository()
        FakeNaifeiRepository()
    }


}