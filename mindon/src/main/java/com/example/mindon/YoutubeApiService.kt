package com.example.mindon

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {

    @GET("search")
    fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("key") apiKey: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10
    ): Call<YoutubeResponse>
}