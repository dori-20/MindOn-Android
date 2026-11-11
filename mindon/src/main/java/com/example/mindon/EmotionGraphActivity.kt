package com.example.mindon

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mindon.databinding.ActivityEmotionGraphBinding
import com.example.mindon.models.EmojiDiary
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class EmotionGraphActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionGraphBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            finish()
        }

        fetchEmotionData()
    }

    private fun fetchEmotionData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users")
            .document(userId)
            .collection("emotions")
            .get()
            .addOnSuccessListener { result ->
                val emotionCount = mutableMapOf<String, Int>()

                for (doc in result) {
                    val diary = doc.toObject(EmojiDiary::class.java)
                    val emotion = diary.emotionType
                    emotionCount[emotion] = emotionCount.getOrDefault(emotion, 0) + 1
                }

                drawBarChart(emotionCount)
            }
            .addOnFailureListener {
                Toast.makeText(this, "데이터 로드 실패", Toast.LENGTH_SHORT).show()
                Log.e("EmotionGraph", "Firestore error", it)
            }
    }

    private fun drawBarChart(emotionCount: Map<String, Int>) {
        val entries = mutableListOf<BarEntry>()
        val labels = listOf("😊", "😢", "😐", "😡") // 기쁨, 슬픔, 그저 그럼, 화남

        val emotions = listOf("기쁨", "슬픔", "그저 그럼", "화남")
        emotions.forEachIndexed { index, emotion ->
            val count = emotionCount[emotion] ?: 0
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
            //labels.add(emotion)
        }

        val dataSet = BarDataSet(entries, "감정 빈도")
        dataSet.colors = listOf(
            Color.parseColor("#B4C9B3"), // 기쁨 - sage green
            Color.parseColor("#A8C3BC"), // 슬픔 - eucalyptus blue
            Color.parseColor("#D8CFC4"), // 그저 그럼 - warm sand
            Color.parseColor("#9BAE95")  // 화남 - olive leaf
        )
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        val chart = binding.lineChart  // layout에 있는 lineChart를 그대로 사용

        chart.data = data
        chart.description.isEnabled = false
        chart.setFitBars(true)
        chart.setTouchEnabled(false)
        chart.animateY(1000)

        // X축 설정
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            granularity = 1f
            setDrawGridLines(false)
            textColor = Color.DKGRAY
            textSize = 12f
        }

        chart.axisLeft.apply {
            axisMinimum = 0f
            setDrawGridLines(true)
        }

        chart.axisRight.isEnabled = false
        chart.invalidate()
    }
}