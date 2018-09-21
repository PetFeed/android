package com.petfeed.petfeed.util

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()

    var userId: String
        get() = preferences.getString("userId", "")
        set(value) {
            editor.putString("userId", value)
            editor.apply()
        }

    var userPassword: String
        get() = preferences.getString("userPassword", "")
        set(value) {
            editor.putString("userPassword", value)
            editor.apply()
        }
}