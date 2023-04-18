package com.example.clothingsuggester.utils

import android.content.SharedPreferences
import com.example.clothingsuggester.model.Bottom
import com.example.clothingsuggester.model.Top
import com.example.clothingsuggester.model.WEATHER
import java.time.Instant

class PrefsManager(private val prefs: SharedPreferences) {

    val savedWeatherOrdinal get() = prefs.getInt(Constants.KEY_PREF_OUTFIT_WEATHER, -1)
    val savedTimestamp get() = prefs.getLong(Constants.KEY_PREF_TIMESTAMP, -1L)
    val savedTopId get() = prefs.getInt(Constants.KEY_PREF_OUTFIT_TOP_ID, -1)
    val savedOldTopId get() = prefs.getInt(Constants.KEY_PREF_OUTFIT_TOP_OLD_ID, -1)
    val savedBottomId get() = prefs.getInt(Constants.KEY_PREF_OUTFIT_BOTTOM_ID, -1)
    val savedOldBottomId get() = prefs.getInt(Constants.KEY_PREF_OUTFIT_BOTTOM_OLD_ID, -1)

    fun saveOutfit(top: Top, bottom: Bottom, weather: WEATHER) {
        val oldTopId = prefs.getInt(Constants.KEY_PREF_OUTFIT_TOP_ID, -1)
        val oldBottomId = prefs.getInt(Constants.KEY_PREF_OUTFIT_BOTTOM_ID, -1)
        prefs.edit().apply {
            if (oldTopId != -1 && oldBottomId != -1) {
                putInt(Constants.KEY_PREF_OUTFIT_TOP_OLD_ID, oldTopId)
                putInt(Constants.KEY_PREF_OUTFIT_BOTTOM_OLD_ID, oldBottomId)
            }
            putInt(Constants.KEY_PREF_OUTFIT_TOP_ID, top.topResId)
            putInt(Constants.KEY_PREF_OUTFIT_BOTTOM_ID, bottom.bottomResId)
            putInt(Constants.KEY_PREF_OUTFIT_WEATHER, weather.ordinal).apply()
            putLong(Constants.KEY_PREF_TIMESTAMP, Instant.now().epochSecond).apply()
        }.apply()


    }

}