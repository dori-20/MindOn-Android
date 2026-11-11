package com.example.mindon

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mindon.custom.TypeWriterTextView
import com.example.mindon.databinding.ActivityMainBinding
import com.example.mindon.databinding.ActivitySplashBinding
import com.kakao.sdk.common.util.Utility.getKeyHash
import java.security.MessageDigest



class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 3초 후 LoginActivity or MainActivity로 전환

        val logo = binding.logo
        val splashText = binding.splashText

        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up_fade)
        logo.startAnimation(slideUp)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.logo.startAnimation(fadeIn)


        // 텍스트 나타나는 애니메이션
        val fadeInDelayed = AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed)
        splashText.visibility = View.VISIBLE
        splashText.startAnimation(fadeIn)


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (MyApplication.checkAuth()) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)

    }


}