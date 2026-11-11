package com.example.mindon

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.mindon.models.EmojiDiary
import com.example.mindon.models.EmojiDiaryLocal

class EmotionDao(private val db: SQLiteDatabase) {

    // 감정일기 저장 (임시 캐시)
    fun insertDiary(diary: EmojiDiary) {
        val values = ContentValues().apply {
            put("emoji", diary.emotionType)
            put("content", diary.content)
            put("timestamp", diary.date)  // 날짜 문자열로 저장
            put("uploaded", 0)
        }
        db.insert("emoji_diary_cache", null, values)
    }

    // 업로드되지 않은 일기 목록 가져오기
    fun getNotUploadedDiaries(): List<EmojiDiaryLocal> {
        val list = mutableListOf<EmojiDiaryLocal>()
        val cursor = db.query("emoji_diary_cache", null, "uploaded = 0", null, null, null, null)

        while (cursor.moveToNext()) {
            list.add(
                EmojiDiaryLocal(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    date = cursor.getString(cursor.getColumnIndexOrThrow("timestamp")),
                    emotionType = cursor.getString(cursor.getColumnIndexOrThrow("emoji")),
                    content = cursor.getString(cursor.getColumnIndexOrThrow("content"))
                )
            )
        }

        cursor.close()
        return list
    }

    // 업로드 완료 표시
    fun markAsUploaded(id: Int) {
        val values = ContentValues().apply {
            put("uploaded", 1)
        }
        db.update("emoji_diary_cache", values, "id = ?", arrayOf(id.toString()))
    }
}