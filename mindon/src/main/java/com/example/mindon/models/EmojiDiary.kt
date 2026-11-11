package com.example.mindon.models

import com.google.firebase.Timestamp


data class EmojiDiary(
    val date: String = "",              // 예: "2025-06-12"
    val emotionType: String = "",       // 예: 😄 😭 😠 😐
    val content: String = "",           // 감정일기 내용
    val timestamp: Timestamp? = null    // Firestore 저장 시간
)
