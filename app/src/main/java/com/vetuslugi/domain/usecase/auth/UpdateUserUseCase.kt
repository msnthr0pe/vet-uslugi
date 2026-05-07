package com.vetuslugi.domain.usecase.auth

import com.vetuslugi.domain.model.User
import com.vetuslugi.domain.repository.AuthRepository

class UpdateUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(user: User): Result<Unit> =
        repository.updateUser(user)
}
