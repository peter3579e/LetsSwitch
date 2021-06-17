package com.peter.letsswtich

import android.app.Application
import android.content.Context
import com.peter.letsswtich.data.source.LetsSwitchRepository
import com.peter.letsswtich.util.ServiceLocator
import kotlin.properties.Delegates

class LetsSwtichApplication : Application() {

    val letsSwitchRepository: LetsSwitchRepository
        get() = ServiceLocator.provideTasksRepository(this)

    companion object {
        var instance: LetsSwtichApplication by Delegates.notNull()
        lateinit var appContext: Context

        fun applicationContext(): Context {
            return instance.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        LetsSwtichApplication.appContext = applicationContext
        instance = this
    }

}