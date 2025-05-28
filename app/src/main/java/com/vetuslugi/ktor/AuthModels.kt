package com.vetuslugi.ktor

import kotlinx.serialization.Serializable

class AuthModels {
    @Serializable
    data class LoginDTO(val email: String)

    data class LoginRequest(
        val login: String,
        val password: String
    )

    @Serializable
    data class UserDTO (
        val login: String,
        val password: String,
        val name: String,
        val surname: String,
        val phone: String,
        val role: String
        )

    data class NewsDTO (
        val title: String,
        val description: String
    )

    data class PlaceDTO (
        val address: String,
        val name: String,
        val phone: String,
        val description: String,
        val owner: String,
    )


    @Serializable
    data class InfoDTO (val info: String)

    data class AuthResponse(
        val token: String
    )
}