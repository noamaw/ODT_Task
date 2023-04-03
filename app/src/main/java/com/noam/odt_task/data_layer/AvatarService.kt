package com.noam.odt_task.data_layer

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AvatarService {
    // get request with added query param for the type of avatar
    @GET("avatar.php?")
    suspend fun getAvatar(@Query("g") g: String): Response<String>
}