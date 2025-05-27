package com.vetuslugi.ktor

import kotlinx.serialization.Serializable

class AuthModels {
    @Serializable
    data class LoginDTO(val email: String)

    data class LoginRequest(
        val login: String,
        val password: String
    )

    data class RegisterRequest(
        val login: String,
        val password: String,
        val name: String,
        val surname: String,
        val phone: String,
        val role: String
        )

    data class ClientRequest(
        val date: String,
        val phone: String,
        val name: String,
        val email: String,
        val time: String,
        val description: String,
    )

    data class NewsDTO (
        val title: String,
        val description: String
    )

    data class SheltersDTO (
        val address: String,
        val name: String,
        val phone: String,
        val description: String,
    )

    @Serializable
    data class ClientByDTO(
        val name: String,
        val date: String,
        val mode: String
    )

    data class AuthResponse(
        val token: String
    )
}