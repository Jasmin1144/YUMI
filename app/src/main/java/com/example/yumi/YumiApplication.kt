package com.example.yumi

import android.app.Application
import android.util.Log

class YumiApplication : Application() {

    private val TAG = "YumiApplication"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "YumiApplication onCreate")
        // TODO: Initialize any application-wide components here
        // (e.g., Dependency Injection like Hilt, Timber for logging, etc.)
    }
}
