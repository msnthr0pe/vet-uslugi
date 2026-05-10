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

    data class PlaceDTO(
        val address: String,
        val name: String,
        val phone: String,
        val description: String,
        val owner: String,
        val clubAddress: String? = null
    )


    @Serializable
    data class InfoDTO (val info: String)

    data class AuthResponse(
        val token: String
    )

    data class AnimalDTO(
        val id: String = "",
        val nickname: String,
        val species: String = "",
        val breed: String = "",
        val age: Int = 0,
        val diseases: String? = null,
        val shelterAddress: String? = null,
        val nurseryAddress: String? = null
    )

    data class SurveyRequest(
        val species: String,
        val breed: String,
        val willingToAdoptSick: Boolean,
        val mostImportant: String
    )

    data class SurveyResultDTO(
        val animal: AnimalDTO,
        val coefficient: Double
    )
}