package com.example.mindon

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mindon.databinding.ActivitySurveyBinding
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurveyActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurveyBinding
    private lateinit var adapter: SurveyAdapter
    private val questionList = QuestionData.questions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurveyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = SurveyAdapter(questionList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.btnSubmit.setOnClickListener {
            if (questionList.any { it.selectedScore == -1 }) {
                Toast.makeText(this, "모든 문항에 응답해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val totalScore = questionList.sumOf { it.selectedScore }
            val apiService = TestRetrofitInstance.api

            apiService.getResult(totalScore).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    val raw = response.body()?.string()
                    Log.d("서버 Raw 응답", raw ?: "null")

                    try {
                        val json = JSONObject(raw ?: "")
                        val message = json.getString("message")

                        ResultDialogFragment
                            .newInstance(message)
                            .show(supportFragmentManager, "ResultDialog")

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@SurveyActivity,
                            "응답 파싱 오류: ${e.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@SurveyActivity,
                        "서버 요청 실패: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("서버실패", "에러 메시지: ${t.localizedMessage}", t)
                }
            })
        }

    }
}
