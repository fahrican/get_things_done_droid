package com.justluxurylifestyle.get_things_done_droid

import android.app.Application
import com.airbnb.epoxy.databinding.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class GetThingsDoneApp: Application() {

    companion object {
        lateinit var instance: GetThingsDoneApp
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}