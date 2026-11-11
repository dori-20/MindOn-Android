package com.example.mindon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import androidx.navigation.Navigation.findNavController
import com.example.mindon.databinding.ActivityMainBinding
import com.example.mindon.databinding.FragmentHomeBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // strings.xml 에 정의한 배열 불러와서 랜덤으로 선택
        val messages = resources.getStringArray(R.array.daily_messages)
        val todayMessage = messages.random()

        // TextView 에 적용
        binding.tvDailyMessage.animateText(todayMessage)
        // 오늘의 감정일기 버튼 → EmotionFragment(두번째 탭)
        binding.btnEmotion.setOnClickListener {
            (activity as? MainActivity)?.moveToPage(1)
        }
        // 센터 위치 버튼 → CenterFragment(세번째 탭)
        binding.btnCenter.setOnClickListener {
            (activity as? MainActivity)?.moveToPage(2)
        }
        // 고민 커뮤니티 버튼 → CommunityFragment(네번째 탭)
        binding.btnCommunity.setOnClickListener {
            (activity as? MainActivity)?.moveToPage(3)
        }
        binding.btnShowGraph.setOnClickListener {
            val intent = Intent(requireContext(), EmotionGraphActivity::class.java)
            startActivity(intent)
        }
        binding.btnVideo.setOnClickListener {
            val intent = Intent(requireContext(), VideoActivity::class.java)
            startActivity(intent)
        }
        binding.btnTest.setOnClickListener {
            val intent = Intent(requireContext(), SurveyActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}