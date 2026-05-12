package com.vetuslugi

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.vetuslugi.di.AppContainer

class VetUslugiApp : Application() {
    val container by lazy { AppContainer(this) }

    override fun onCreate() {
        super.onCreate()
        val isDark = getSharedPreferences("credentials", Context.MODE_PRIVATE)
            .getBoolean("isDarkTheme", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
