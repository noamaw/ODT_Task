package com.noam.odt_task

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ODTApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}