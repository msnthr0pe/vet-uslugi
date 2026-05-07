package com.vetuslugi.domain.usecase.auth

import com.vetuslugi.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(login: String, password: String): Result<Unit> =
        repository.login(login, password)
}
