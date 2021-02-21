package com.charuniverse.kelasku

import android.app.Application
import com.charuniverse.kelasku.util.AppPreferences

class Kelasku : Application() {
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)
    }
}