package com.vetuslugi.data.repository

import com.vetuslugi.domain.model.User
import com.vetuslugi.domain.repository.AuthRepository
import com.vetuslugi.ktor.AuthApi
import com.vetuslugi.ktor.AuthModels

class AuthRepositoryImpl(private val api: AuthApi) : AuthRepository {

    override suspend fun login(login: String, password: String): Result<Unit> =
        runCatching { api.login(AuthModels.LoginRequest(login, password)) }.map {}

    override suspend fun register(user: User): Result<Unit> =
        runCatching {
            api.register(
                AuthModels.UserDTO(user.login, user.password, user.name, user.surname, user.phone, user.role)
            )
        }.map {}

    override suspend fun getUser(login: String): Result<User> =
        runCatching {
            val dto = api.getUser(AuthModels.LoginDTO(login))
            User(dto.login, dto.password, dto.name, dto.surname, dto.phone, dto.role)
        }

    override suspend fun updateUser(user: User): Result<Unit> =
        runCatching {
            api.updateUser(
                AuthModels.UserDTO(user.login, user.password, user.name, user.surname, user.phone, user.role)
            )
        }.map {}
}
