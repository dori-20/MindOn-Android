package com.example.mindon

data class Question(
    val id: Int,
    val text: String,
    var selectedScore: Int = -1
)