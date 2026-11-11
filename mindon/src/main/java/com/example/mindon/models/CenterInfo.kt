package com.example.mindon.models

data class CenterInfo(
    val centerId: String,  // 공공 API 고유 ID
    val name: String,
    val address: String,
    val phone: String
)
