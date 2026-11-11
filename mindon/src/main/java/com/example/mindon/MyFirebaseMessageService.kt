package com.example.mindon

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessageService : FirebaseMessagingService() {

    //서버가 실행될때마다 자동으로 실행
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if(message.data.isNotEmpty()){ //메세지가 있다면
            Log.d("25android","${message.data}")

            //메세지를 받았다는걸 알림을 보여주기
            //연결해줌
            val helper = MyNoticicationHelper(this)
            helper.showNotification(message.data.get("title"), message.data.get("value")) //node_fcm_server.js data 안에 두 개의 키 값을 가지고 있음
        }
    }
}