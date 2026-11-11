package com.example.mindon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mindon.databinding.FragmentEmotionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.mindon.models.EmojiDiary
import com.google.firebase.Timestamp

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EmotionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EmotionFragment : Fragment() {

    private lateinit var binding: FragmentEmotionBinding
    private lateinit var adapter: EmotionAdapter
    private val emotionList = mutableListOf<EmojiDiary>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmotionBinding.inflate(inflater, container, false)

        adapter = EmotionAdapter(emotionList) { entry ->
            showEmotionDialog(entry)
        }
        binding.recyclerViewEmotion.adapter = adapter
        binding.recyclerViewEmotion.layoutManager = GridLayoutManager(requireContext(), 3)
        loadEmotionsFromFirestore()

        binding.btnWriteEmotion.setOnClickListener {
            val intent = Intent(requireContext(), EmotionWriteActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    private fun showEmotionDialog(entry: EmojiDiary) {
        AlertDialog.Builder(requireContext())
            .setTitle("${entry.date}의 감정일기")
            .setMessage("기분: ${entry.emotionType}\n\n내용:\n${entry.content}")
            .setPositiveButton("닫기", null)
            .show()
    }
    override fun onResume() {
        super.onResume()
        syncLocalCachedDiaries()  // 임시저장 불러오기 SQLite → Firestore
        loadEmotionsFromFirestore()
    }
    private fun loadEmotionsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("emotions")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                emotionList.clear()
                for (document in result) {
                    val entry = document.toObject(EmojiDiary::class.java)
                    emotionList.add(entry)
                }

                context?.let { ctx ->
                    val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
                    val sortedList = if (prefs.getBoolean("enable_emotion_sort", true)) {
                        val sortOrder = prefs.getString("emotion_sort_order", "latest")
                        when (sortOrder) {
                            "latest" -> emotionList.sortedByDescending { it.date }
                            "oldest" -> emotionList.sortedBy { it.date }
                            "emotion" -> emotionList.sortedBy { it.emotionType }
                            else -> emotionList
                        }
                    } else {
                        emotionList
                    }

                    emotionList.clear()
                    emotionList.addAll(sortedList)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                Log.e("EmotionFragment", "Firestore 불러오기 실패", e)
            }
    }

    private fun syncLocalCachedDiaries() {
        val context = requireContext()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbHelper = DBHelper(context)
        val db = dbHelper.writableDatabase
        val dao = EmotionDao(db)

        val unsynced = dao.getNotUploadedDiaries()
        val firestore = FirebaseFirestore.getInstance()

        for (diary in unsynced) {
            val entry = EmojiDiary(
                date = diary.date,
                emotionType = diary.emotionType,
                content = diary.content,
                timestamp = Timestamp.now()
            )

            firestore.collection("users")
                .document(userId)
                .collection("emotions")
                .document(diary.date)
                .set(entry)
                .addOnSuccessListener {
                    Log.d("Sync", "업로드 완료: ${diary.date}")
                    dao.markAsUploaded(diary.id)
                }
                .addOnFailureListener {
                    Log.e("Sync", "업로드 실패: ${diary.date}", it)
                }
        }
    }

}