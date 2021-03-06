package com.lq.joy

import android.app.Application
import com.lq.joy.data.AppContainer
import com.lq.joy.data.AppContainerImpl

class JoyApplication : Application() {


    companion object {
        lateinit var context:Application
    }

    lateinit var container: AppContainer


    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        context = this
    }


}