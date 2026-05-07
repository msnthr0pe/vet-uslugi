package com.vetuslugi.domain.repository

import com.vetuslugi.domain.model.User

interface AuthRepository {
    suspend fun login(login: String, password: String): Result<Unit>
    suspend fun register(user: User): Result<Unit>
    suspend fun getUser(login: String): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
}
