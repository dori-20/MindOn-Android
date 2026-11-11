package com.example.mindon

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface TestApiService {
    @GET("depression_result.php")
    fun getResult(@Query("score") score: Int): Call<ResponseBody>
}