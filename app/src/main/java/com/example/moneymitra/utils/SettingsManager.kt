package com.example.moneymitra.utils

import android.content.Context

object SettingsManager {

    fun saveAutoRead(context: Context, enabled: Boolean) {

        val prefs =
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        prefs.edit()
            .putBoolean("auto_read_notifications", enabled)
            .apply()
    }

    fun isAutoReadEnabled(context: Context): Boolean {

        val prefs =
            context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        return prefs.getBoolean("auto_read_notifications", false)
    }
}