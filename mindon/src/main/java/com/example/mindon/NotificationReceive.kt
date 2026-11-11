package com.example.mindon

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.util.Log

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("NotificationReceiver", "알림 리시버 실행됨!")

        val builder = NotificationCompat.Builder(context, "emotion_channel")
            .setSmallIcon(R.drawable.alarm)
            .setContentTitle("오늘의 감정일기")
            .setContentText("하루를 돌아보며 감정을 기록해보세요 💚")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1001, builder.build())
        }
    }
}