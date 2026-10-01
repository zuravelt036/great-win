package com.ZfQnLmK.vJxRpT.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesStorage(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun stars(schemeId: String): Int = preferences.getInt(KEY_STARS + schemeId, 0)

    fun bestScore(schemeId: String): Int = preferences.getInt(KEY_SCORE + schemeId, 0)

    fun storeResult(schemeId: String, stars: Int, score: Int) {
        val editor = preferences.edit()
        if (stars > stars(schemeId)) editor.putInt(KEY_STARS + schemeId, stars)
        if (score > bestScore(schemeId)) editor.putInt(KEY_SCORE + schemeId, score)
        editor.apply()
    }

    fun activeSchemeId(fallback: String): String =
        preferences.getString(KEY_ACTIVE, fallback) ?: fallback

    fun setActiveSchemeId(id: String) {
        preferences.edit().putString(KEY_ACTIVE, id).apply()
    }

    fun flag(key: String, fallback: Boolean): Boolean = preferences.getBoolean(key, fallback)

    fun setFlag(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    companion object {
        private const val STORE_NAME = "great_win_store"
        private const val KEY_STARS = "stars_"
        private const val KEY_SCORE = "score_"
        private const val KEY_ACTIVE = "active_scheme"
        const val KEY_SOUND = "flag_sound"
        const val KEY_VIBRATION = "flag_vibration"
        const val KEY_CONTRAST = "flag_contrast"
    }
}
