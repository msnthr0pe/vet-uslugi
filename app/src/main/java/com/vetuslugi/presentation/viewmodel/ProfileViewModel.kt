package com.vetuslugi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vetuslugi.data.local.UserSession
import com.vetuslugi.domain.model.Place
import com.vetuslugi.domain.model.User
import com.vetuslugi.domain.usecase.place.GetNurseriesByOwnerUseCase
import com.vetuslugi.domain.usecase.place.GetSheltersByOwnerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getSheltersByOwnerUseCase: GetSheltersByOwnerUseCase,
    private val getNurseriesByOwnerUseCase: GetNurseriesByOwnerUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _shelters = MutableStateFlow<List<Place>>(emptyList())
    val shelters: StateFlow<List<Place>> = _shelters

    private val _nurseries = MutableStateFlow<List<Place>>(emptyList())
    val nurseries: StateFlow<List<Place>> = _nurseries

    val currentUser: User? get() = userSession.getUser()

    fun loadPlaces() {
        val login = userSession.getLogin() ?: return
        viewModelScope.launch {
            getSheltersByOwnerUseCase(login).onSuccess { _shelters.value = it }
            getNurseriesByOwnerUseCase(login).onSuccess { _nurseries.value = it }
        }
    }
}
