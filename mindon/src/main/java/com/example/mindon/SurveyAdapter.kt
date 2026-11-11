package com.example.mindon

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mindon.databinding.ItemQuestionBinding

class SurveyAdapter(private val questions: List<Question>) :
    RecyclerView.Adapter<SurveyAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(val binding: ItemQuestionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val question = questions[position]
        val b = holder.binding

        b.tvQuestion.text = "${position + 1}. ${question.text}"

        // 점수 선택 시 저장
        b.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            question.selectedScore = when (checkedId) {
                b.rb1.id -> 3 // 항상 그렇다
                b.rb2.id -> 2 // 자주 그렇다
                b.rb3.id -> 1 // 가끔 그렇다
                b.rb4.id -> 0 // 아니다
                else -> -1
            }
        }

        // 기존 선택값 유지 (스크롤 시 문제 방지)
        when (question.selectedScore) {
            3 -> b.radioGroup.check(b.rb1.id)
            2 -> b.radioGroup.check(b.rb2.id)
            1 -> b.radioGroup.check(b.rb3.id)
            0 -> b.radioGroup.check(b.rb4.id)
            else -> b.radioGroup.clearCheck()
        }
    }

    override fun getItemCount(): Int = questions.size
}