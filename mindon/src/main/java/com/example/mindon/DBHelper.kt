package com.example.mindon

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "mindon.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {

        //일기 임시저장
        db.execSQL(
            """CREATE TABLE emoji_diary_cache (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT,
                emoji TEXT,
                content TEXT,
                timestamp TEXT,
                uploaded INTEGER DEFAULT 0
            )"""
        )

        // 즐겨찾기 기관 저장 테이블
        db.execSQL(
            """CREATE TABLE favorite_centers (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            center_id TEXT UNIQUE,
            name TEXT,
            address TEXT,
            phone TEXT
        )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS emoji_diary_cache")
        onCreate(db)
    }



}