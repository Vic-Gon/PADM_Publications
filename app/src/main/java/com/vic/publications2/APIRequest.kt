package com.vic.publications2

import com.vic.publications2.api.BeiraPtApiJSON
import retrofit2.http.GET

interface APIRequest {

    @GET("/wp-json/custom/v1/posts")
    suspend fun getNews() : BeiraPtApiJSON
}