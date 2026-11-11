package com.example.mindon

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.example.mindon.models.CenterInfo

class FavoriteDao(private val db: SQLiteDatabase) {

    fun insertFavorite(center: CenterInfo) {
        val values = ContentValues().apply {
            put("center_id", center.centerId)
            put("name", center.name)
            put("address", center.address)
            put("phone", center.phone)
        }
        db.insertWithOnConflict("favorite_centers", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    fun deleteFavorite(centerId: String) {
        db.delete("favorite_centers", "center_id = ?", arrayOf(centerId))
    }

    fun getAllFavorites(): List<CenterInfo> {
        val list = mutableListOf<CenterInfo>()
        val cursor = db.query("favorite_centers", null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            list.add(
                CenterInfo(
                    centerId = cursor.getString(cursor.getColumnIndexOrThrow("center_id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    address = cursor.getString(cursor.getColumnIndexOrThrow("address")),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"))
                )
            )
        }
        Log.d("즐겨찾기불러오기", "불러온 개수: ${list.size}")
        cursor.close()
        return list
    }

    fun isFavorite(centerId: String): Boolean {
        val cursor = db.query(
            "favorite_centers",
            arrayOf("center_id"),
            "center_id = ?",
            arrayOf(centerId),
            null,
            null,
            null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }
}
