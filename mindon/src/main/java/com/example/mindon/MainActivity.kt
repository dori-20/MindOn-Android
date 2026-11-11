package com.example.mindon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.mindon.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.messaging
import java.security.MessageDigest
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    // Fragment 목록 -순서 맞게 해야함
    private val fragmentList = listOf(
        HomeFragment(),
        EmotionFragment(),
        CenterFragment(),
        CommunityFragment(),
        SettingsFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // NavigationView에서 헤더뷰 접근
        val headerView = binding.navigationView.getHeaderView(0)
        val headerText = headerView.findViewById<TextView>(R.id.navHeaderMessage)

        // 시간대 기반 인사말
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val nickname = FirebaseAuth.getInstance().currentUser?.displayName ?: "사용자"
        val greeting = when (hour) {
            in 5..11 -> "좋은 아침이에요, $nickname 님 ☀️"
            in 12..17 -> "오늘 하루도 힘내세요, $nickname 님 💪"
            in 18..22 -> "편안한 저녁 되세요, $nickname 님 🌙"
            else -> "늦은 시간에도 찾아주셔서 고마워요, $nickname 님 🌌"
        }

        headerText.text = greeting


//        Firebase.messaging.token.addOnSuccessListener{
//            Log.d("mindon token", it) //it은 현재 전달받은 값
//             }

        //알림 채널 생성
        createNotificationChannel()
        Log.d("MainActivity", "알림 채널 생성 완료")


        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if(it.all{ permission -> permission.value != true }){
                //하나라도 true가 아닌게 있다면
                Toast.makeText(this,"permission DENIED", Toast.LENGTH_SHORT).show()
            } //모두에 대해서 승인을 해줘야함. 그래야 가능
        }
        //알림에 대한 승인 해줘야함.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            //이상이면 notification에 대한 승인이 필요함.
            //사용자가 승인했는가의 확인이 필요함
            if(ContextCompat.checkSelfPermission(
                    this,
                    "android.permission.POST_NOTIFICATIONS"
                )!= PackageManager.PERMISSION_GRANTED){
                //승인 요청
                permissionLauncher.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
            }
        }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
            val granted = checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            Log.d("PermissionCheck", "알림 권한 상태: $granted")  // PERMISSION_GRANTED == 0
        }

        val drawerLayout = binding.drawerLayout
        val navigationView = binding.navigationView
        // Firebase 인증 초기화
        auth = FirebaseAuth.getInstance()

        // Google 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("331740237259-18jh6ap2g9g2mh6co073d758tb9un7bb.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // ViewPager2 + 어댑터 연결
        val pagerAdapter = MyFragmentStateAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        val prefs = getSharedPreferences("com.example.mindon_preferences", Context.MODE_PRIVATE)
        val savedIndex = if (prefs.getBoolean("remember_last_tab", true)) {
            prefs.getInt("last_tab_index", 0)
        } else {
            0
        }
        binding.viewPager.currentItem = savedIndex

        // TabLayout + ViewPager2 연결
        val tabTitles = listOf("홈", "감정일기", "센터", "커뮤니티", "설정")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val prefs = getSharedPreferences("com.example.mindon_preferences", Context.MODE_PRIVATE)
                val rememberTab = prefs.getBoolean("remember_last_tab", true)
                if (rememberTab) {
                    prefs.edit().putInt("last_tab_index", position).apply()
                }
            }
        })

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    // 로그아웃 처리
                    auth.signOut()
                    googleSignInClient.signOut().addOnCompleteListener {
                        com.kakao.sdk.user.UserApiClient.instance.logout { error ->
                            if (error != null) {
                                Log.e("Logout", "카카오 로그아웃 실패: $error")
                            } else {
                                Log.d("Logout", "카카오 로그아웃 성공")
                            }
                            Toast.makeText(this, "모든 계정에서 로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    }
                    binding.drawerLayout.closeDrawers()
                    true
                }
                R.id.nav_setting -> {
                    // 설정 탭으로 이동
                    moveToPage(4) // SettingsFragment로 이동(인덱스 맞게)
                    binding.drawerLayout.closeDrawers()
                    true
                }

                else -> false
            }
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "감정일기 알림"
            val descriptionText = "하루 감정일기 리마인더"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("emotion_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // HomeFragment 등에서 탭 전환하고 싶을 때 쓸 함수
    fun moveToPage(index: Int) {
        binding.viewPager.currentItem = index
    }

}