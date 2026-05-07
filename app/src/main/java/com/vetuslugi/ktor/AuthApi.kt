package com.vetuslugi.ktor

import com.vetuslugi.ktor.AuthModels.LoginDTO
import com.vetuslugi.ktor.AuthModels.NewsDTO
import com.vetuslugi.ktor.AuthModels.PlaceDTO
import com.vetuslugi.ktor.AuthModels.UserDTO
import com.vetuslugi.ktor.AuthModels.AuthResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(@Body request: AuthModels.LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: UserDTO): AuthResponse

    @POST("getuser")
    suspend fun getUser(@Body email: LoginDTO): UserDTO

    @GET("getnews")
    suspend fun getNews(): List<NewsDTO>

    @POST("addnews")
    suspend fun addNews(@Body news: NewsDTO): AuthResponse

    @GET("getshelters")
    suspend fun getShelters(): List<PlaceDTO>

    @GET("getnurseries")
    suspend fun getNurseries(): List<PlaceDTO>

    @POST("updateshelter")
    suspend fun updateShelter(@Body request: PlaceDTO): AuthModels.InfoDTO

    @POST("updatenursery")
    suspend fun updateNursery(@Body request: PlaceDTO): AuthModels.InfoDTO

    @POST("getshelterby")
    suspend fun getShelterBy(@Body request: AuthModels.InfoDTO): List<PlaceDTO>

    @POST("getnurseryby")
    suspend fun getNurseryBy(@Body request: AuthModels.InfoDTO): List<PlaceDTO>

    @POST("addshelter")
    suspend fun addShelter(@Body news: PlaceDTO): AuthResponse

    @POST("addnursery")
    suspend fun addNursery(@Body news: PlaceDTO): AuthResponse

    @POST("updateuser")
    suspend fun updateUser(@Body request: UserDTO): AuthResponse
}
