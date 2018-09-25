package com.petfeed.petfeed.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PrefManager(context: Context) {
    private var preferences: SharedPreferences = context.getSharedPreferences("Data", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = preferences.edit()
    var gson = Gson()
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

    var checkedLogs: ArrayList<String> = ArrayList()
        get() = gson.fromJson<ArrayList<String>>(preferences.getString("checkedLogs", "[]"), object : TypeToken<ArrayList<String>>() {}.type)
        private set

    fun addCheckedLogs(logId: String) {
        val logs = checkedLogs
        if (!logs.contains(logId)) {
            logs.add(logId)
            editor.putString("checkedLogs", gson.toJson(logs))
            editor.apply()
        }
    }
}