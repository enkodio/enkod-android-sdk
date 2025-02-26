package com.enkod.androidsdk.utils

import android.content.Context
import com.enkod.androidsdk.utils.Preferences.TAG

class Prefs(context: Context) {

    private val preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)

    fun <T> putValue(name: String, value: T) {
        val editor = preferences.edit()
        when (value) {
            is String -> editor.putString(name, value)
            is Int -> editor.putInt(name, value)
            is Boolean -> editor.putBoolean(name, value)
            is Float -> editor.putFloat(name, value)
            is Long -> editor.putLong(name, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
        editor.apply()
    }

    fun <T> getValue(name: String, type: T): T? {
        val result: Any? = when (type) {
            is String -> preferences.getString(name, null)
            is Int -> preferences.getInt(name, -1)
            is Boolean -> preferences.getBoolean(name, false)
            is Float -> preferences.getFloat(name, -1f)
            is Long -> preferences.getLong(name, -1)
            else -> null
        }
        return result as? T
    }
}