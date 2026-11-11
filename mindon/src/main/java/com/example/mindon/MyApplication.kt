package com.example.mindon

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "7cef7d596764bf7180939dc5011c35bf")
    }
    companion object {
        fun checkAuth(): Boolean {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            return user != null
        }
    }
}