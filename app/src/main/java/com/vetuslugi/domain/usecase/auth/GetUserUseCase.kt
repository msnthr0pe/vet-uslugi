package com.vetuslugi.domain.usecase.auth

import com.vetuslugi.domain.model.User
import com.vetuslugi.domain.repository.AuthRepository

class GetUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(login: String): Result<User> =
        repository.getUser(login)
}
