package com.lq.joy.data

import android.content.Context
import com.lq.joy.data.sakura.ISakuraRepository
import com.lq.joy.data.sakura.SakuraRepository


interface AppContainer {
    val sakuraRepository: ISakuraRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    override val sakuraRepository: ISakuraRepository by lazy { SakuraRepository(applicationContext) }
}