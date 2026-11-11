package com.example.mindon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.et_email)
        val pwField = findViewById<EditText>(R.id.et_password)
        val loginBtn = findViewById<Button>(R.id.btn_login)
        val signupBtn = findViewById<Button>(R.id.btn_signup)
        val errorText = findViewById<TextView>(R.id.tv_error_message)
        val googleBtn = findViewById<ImageView>(R.id.btn_google_signin)
        val kakaoBtn = findViewById<ImageView>(R.id.btn_kakao_login)


// 로그인 버튼 클릭 시
        loginBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = pwField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(this, "로그인 성공: ${user?.email}", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            errorText.text = "로그인 실패: 이메일 또는 비밀번호 확인"
                            errorText.visibility = View.VISIBLE
                        }
                    }
            } else {
                errorText.text = "이메일과 비밀번호를 입력해주세요"
                errorText.visibility = View.VISIBLE
            }
        }

        //회원가입 버튼 클릭
        signupBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        // Google 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("331740237259-18jh6ap2g9g2mh6co073d758tb9un7bb.apps.googleusercontent.com") // Firebase 콘솔에서 발급
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼 클릭 이벤트

        googleBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        //카카오로그인

        kakaoBtn.setOnClickListener {
            val loginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                Log.d("KakaoLogin", "카카오 콜백 실행됨")

                if (error != null) {
                    Log.e("KakaoLogin", "로그인 실패: $error")
                    Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
                } else if (token != null) {
                    Log.d("KakaoLogin", "토큰 수신 성공: ${token.accessToken}")
                    handleKakaoLogin(token)
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                Log.d("KakaoLogin", "카카오톡 로그인 시도")
                UserApiClient.instance.loginWithKakaoTalk(this, callback = loginCallback)
            } else {
                Log.d("KakaoLogin", "카카오 계정 로그인 시도")
                UserApiClient.instance.loginWithKakaoAccount(this, callback = loginCallback)
            }
        }
    }



    // 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "구글 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleKakaoLogin(token: OAuthToken) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패: $error")
                Toast.makeText(this, "카카오 사용자 정보 요청 실패", Toast.LENGTH_SHORT).show()
            } else if (user != null) {
                val kakaoId = user.id.toString()
                val nickname = user.kakaoAccount?.profile?.nickname ?: "익명"
                Log.d("KakaoLogin", "카카오 로그인 성공: id=$kakaoId, nickname=$nickname")
                requestFirebaseCustomToken(kakaoId)
                Toast.makeText(this, "환영합니다: $nickname", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Log.e("KakaoLogin", "user == null 이지만 error도 없음")
            }
        }
    }

    private fun requestFirebaseCustomToken(kakaoId: String) {
        Log.d("TokenRequest", "요청 시작 - UID: $kakaoId")
        val client = OkHttpClient()
        val json = JSONObject().put("kakaoUid", kakaoId).toString()
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("http://10.0.2.2:3000/customToken") // 에뮬레이터는 localhost → 10.0.2.2
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val token = JSONObject(responseBody!!).getString("token")
                    runOnUiThread {
                        signInWithFirebase(token)
                    }
                } else {
                    Log.e("TokenRequest", "서버 오류: ${response.code}")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.e("TokenRequest", "요청 실패", e)
            }
        })
    }

    private fun signInWithFirebase(customToken: String) {
        Log.d("FirebaseCustomLogin", " 커스텀 토큰으로 로그인 시도: $customToken")
        FirebaseAuth.getInstance().signInWithCustomToken(customToken)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    Toast.makeText(this, "Firebase 로그인 성공: ${user?.uid}", Toast.LENGTH_SHORT).show()

                    // Firestore 저장도 가능
                    val userInfo = hashMapOf(
                        "uid" to user?.uid,
                        "provider" to "kakao"
                    )
                    Firebase.firestore.collection("users").document(user!!.uid).set(userInfo)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Firebase 로그인 실패", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseLogin", "로그인 실패", task.exception)
                }
            }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        Toast.makeText(this, "로그인 성공: ${user.displayName}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "이메일 인증이 필요합니다. 메일함 을 확인해주세요.",
                            Toast.LENGTH_LONG
                        ).show()
                        auth.signOut() // 인증되지 않은 계정은 바로 로그아웃
                    }
                } else {
                    Toast.makeText(this, "Firebase 인증 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
