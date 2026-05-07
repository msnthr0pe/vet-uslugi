package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.vetuslugi.data.local.UserSession
import com.vetuslugi.domain.model.User

class ProfileInfoViewModel(private val userSession: UserSession) : ViewModel() {
    val currentUser: User? get() = userSession.getUser()
}
