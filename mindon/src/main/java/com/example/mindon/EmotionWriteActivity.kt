package com.example.mindon

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mindon.databinding.ActivityEmotionWriteBinding
import com.example.mindon.models.EmojiDiary
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmotionWriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmotionWriteBinding
    private var selectedEmoji: String = ""
    private lateinit var emojiButtons: List<ImageView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmotionWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emojiButtons = listOf(binding.imgHappy, binding.imgSad, binding.imgSoso, binding.imgAngry)





        // 이모지 선택 버튼 클릭
        binding.imgHappy.setOnClickListener { updateEmojiSelection("기쁨", binding.imgHappy) }
        binding.imgSad.setOnClickListener { updateEmojiSelection("슬픔", binding.imgSad) }
        binding.imgSoso.setOnClickListener { updateEmojiSelection("그저 그럼", binding.imgSoso) }
        binding.imgAngry.setOnClickListener { updateEmojiSelection("화남", binding.imgAngry) }

        // 날짜 기본값 및 클릭 시 DatePicker
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Calendar.getInstance().time)
        binding.editDate.setText(today)
        binding.editDate.setOnClickListener { showDatePicker() }

        // 저장 버튼
        binding.btnSave.setOnClickListener { saveToFirestore() }

        // 뒤로가기
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateEmojiSelection(emoji: String, selectedImage: ImageView) {
        selectedEmoji = emoji
        emojiButtons.forEach {
            it.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        }
        selectedImage.setBackgroundColor(ContextCompat.getColor(this, R.color.sage_green))
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                binding.editDate.setText(formatter.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveToFirestore() {
        val date = binding.editDate.text.toString()
        val content = binding.editContent.text.toString()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (selectedEmoji.isEmpty() || date.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "모든 항목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        val entry = EmojiDiary(
            date = date,
            emotionType = selectedEmoji,
            content = content,
            timestamp = Timestamp.now()
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(userId)
            .collection("emotions")
            .document(date)
            .set(entry)
            //FireStore 저장 성공 시
            .addOnSuccessListener {
                Toast.makeText(this, "저장 완료!", Toast.LENGTH_SHORT).show()
                finish()
            }
            //실패시에 오프라인으로 임시 저장
            .addOnFailureListener { e ->
                Toast.makeText(this, "저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "저장 오류", e)

                // 🔻 SQLite에 임시 저장
                val localDb = DBHelper(this).writableDatabase
                val dao = EmotionDao(localDb)
                dao.insertDiary(entry)

                Toast.makeText(this, "임시 저장됨 (오프라인 모드)", Toast.LENGTH_SHORT).show()
            }
    }
}
