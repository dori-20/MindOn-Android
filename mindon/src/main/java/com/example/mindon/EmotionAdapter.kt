package com.example.mindon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mindon.models.EmojiDiary


class EmotionAdapter(private val items: List<EmojiDiary>, private val onItemClick: (EmojiDiary) -> Unit) :
    RecyclerView.Adapter<EmotionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiView: ImageView = itemView.findViewById(R.id.imgEmotion)
        val dateView: TextView = itemView.findViewById(R.id.textDate)
        init {
            itemView.setOnClickListener {
                onItemClick(items[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emotion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 감정 문자열을 이미지 리소스로 변환
        val emotionRes = when (item.emotionType) {
            "기쁨" -> R.drawable.happy
            "슬픔" -> R.drawable.sad
            "그저 그럼" -> R.drawable.soso
            "화남" -> R.drawable.angry
            else -> R.drawable.what// 예비 이미지
        }

        holder.emojiView.setImageResource(emotionRes)
        holder.dateView.text = item.date.substring(5)  // "MM-DD" 형태로 자르기
    }

    override fun getItemCount(): Int = items.size

    private fun getEmotionImageRes(emotion: String): Int {
        return when (emotion) {
            "기쁨" -> R.drawable.happy
            "슬픔" -> R.drawable.sad
            "그저 그럼" -> R.drawable.soso
            "화남" -> R.drawable.angry
            else -> R.drawable.what // 기본값 설정
        }
    }

}

