package com.vetuslugi.ktor

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface ImgBBApi {
    @FormUrlEncoded
    @POST("upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String,
        @Field("image") base64Image: String
    ): ImgBBResponse
}

data class ImgBBResponse(val data: ImgBBData, val success: Boolean)
data class ImgBBData(val url: String)
