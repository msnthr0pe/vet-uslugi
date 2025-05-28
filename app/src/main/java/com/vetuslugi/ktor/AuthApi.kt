package com.vetuslugi.ktor

import com.vetuslugi.ktor.AuthModels.LoginDTO
import com.vetuslugi.ktor.AuthModels.NewsDTO
import com.vetuslugi.ktor.AuthModels.PlaceDTO
import com.vetuslugi.ktor.AuthModels.UserDTO
import com.vetuslugi.ktor.AuthModels.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login(@Body request: AuthModels.LoginRequest): Call<AuthModels.AuthResponse>

    @POST("register")
    fun register(@Body request: UserDTO): Call<AuthResponse>

    @POST("getuser")
    suspend fun getUser(@Body email: LoginDTO): UserDTO

    @GET("getnews")
    suspend fun getNews(): List<NewsDTO>

    @POST("addnews")
    fun addNews(@Body news: NewsDTO): Call<AuthResponse>

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
    fun addShelter(@Body news: PlaceDTO): Call<AuthResponse>

    @POST("addnursery")
    fun addNursery(@Body news: PlaceDTO): Call<AuthResponse>

    @POST("updateuser")
    suspend fun updateUser(@Body request: UserDTO): AuthResponse

}