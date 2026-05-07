package com.vetuslugi.data.local

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("search_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun load(): List<String> {
        val json = prefs.getString("history", null) ?: return emptyList()
        return gson.fromJson(json, object : TypeToken<MutableList<String>>() {}.type)
    }

    fun addQuery(query: String) {
        val history = load().toMutableList()
        history.remove(query)
        history.add(0, query)
        save(history.take(10))
    }

    fun save(history: List<String>) {
        prefs.edit().putString("history", gson.toJson(history)).apply()
    }

    fun clear() {
        prefs.edit().remove("history").apply()
    }
}
