package com.example.utils

import android.content.Context

class SharedPref constructor(val context: Context) {

    val sharedPref by lazy { context.getSharedPreferences("sharedPref", Context.MODE_PRIVATE) }

    fun saveLastPageNumber(key: String, data: Int) {
        sharedPref.edit().putInt(key, data).apply()
    }

    fun getLastPageNumber(key: String): Int {
        return sharedPref.getInt(key, 0)
    }

    var isOneCreate: Boolean
        get() {
            return sharedPref.getBoolean("KEY2", true)
        }
        set(value) {
            sharedPref.edit().putBoolean("KEY2", value).apply()
        }
}