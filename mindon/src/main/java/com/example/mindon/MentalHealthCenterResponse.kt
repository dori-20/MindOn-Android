package com.example.mindon

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml
import retrofit2.http.GET
import retrofit2.http.Query

@Xml(name = "MindHealthPromotionCenter")  // 복지센터 전용 루트
data class MentalHealthCenterResponse(
    @Element(name = "row")
    val rows: List<GgItem>
)