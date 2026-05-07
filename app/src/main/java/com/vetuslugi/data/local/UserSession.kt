package com.vetuslugi.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.vetuslugi.domain.model.User

class UserSession(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("credentials", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit {
            putString("login", user.login)
            putString("name", user.name)
            putString("surname", user.surname)
            putString("phone", user.phone)
            putString("password", user.password)
            putString("role", user.role)
        }
    }

    fun getUser(): User? {
        val login = prefs.getString("login", null) ?: return null
        return User(
            login = login,
            password = prefs.getString("password", "") ?: "",
            name = prefs.getString("name", "") ?: "",
            surname = prefs.getString("surname", "") ?: "",
            phone = prefs.getString("phone", "") ?: "",
            role = prefs.getString("role", "") ?: ""
        )
    }

    fun getLogin(): String? = prefs.getString("login", null)
    fun getRole(): String = prefs.getString("role", "-") ?: "-"
}
