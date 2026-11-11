package com.example.mindon

import com.example.mindon.GgResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CenterApiService {
    //정신의료기관
    @GET("Ggmindmedinst")
    fun getMentalCenters(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 100,
        @Query("type") type: String = "xml",
        @Query("SIGUN_NM") sigunName: String
    ): Call<GgResponse>

    // 정신건강복지센터
    @GET("MindHealthPromotionCenter")
    fun getMentalHealthCenters(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 100,
        @Query("type") type: String = "xml",
        @Query("SIGUN_NM") sigunName: String
    ): Call<MentalHealthCenterResponse>
}

