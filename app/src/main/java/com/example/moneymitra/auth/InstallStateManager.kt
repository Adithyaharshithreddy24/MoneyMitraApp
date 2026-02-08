package com.example.moneymitra.auth

import android.content.Context

class InstallStateManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("install_prefs", Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean {
        val firstLaunch = prefs.getBoolean("first_launch", true)
        if (firstLaunch) {
            prefs.edit().putBoolean("first_launch", false).apply()
        }
        return firstLaunch
    }
}
