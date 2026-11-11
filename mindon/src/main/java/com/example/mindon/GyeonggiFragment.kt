package com.example.mindon

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mindon.databinding.FragmentGyeonggiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GyeonggiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GyeonggiFragment : Fragment() {

    private lateinit var binding: FragmentGyeonggiBinding
    private val TAG = "GyeonggiAPI"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGyeonggiBinding.inflate(inflater, container, false)

        val loc = arguments?.getString("searchloc") ?: ""
        val type = arguments?.getString("searchtype") ?: "의료기관"
        Log.d("GyeonggiDebug", "type=$type, loc=$loc")

        if (loc.isBlank()) {
            Toast.makeText(requireContext(), "시군명을 전달받지 못했습니다.", Toast.LENGTH_SHORT).show()
            return binding.root
        }

        // API 분기
        if (type == "복지센터") {
            val call: Call<MentalHealthCenterResponse> =
                RetrofitConnection.xmlNetworkService.getMentalHealthCenters(
                    serviceKey = "a0de3e659642483285f1601608e33208",
                    pageNo = 1,
                    numOfRows = 500,
                    type = "xml",
                    sigunName = loc
                )

            call.enqueue(object : Callback<MentalHealthCenterResponse> {
                override fun onResponse(call: Call<MentalHealthCenterResponse>, response: Response<MentalHealthCenterResponse>) {
                    if (response.isSuccessful) {
                        val items = response.body()?.rows ?: emptyList()
                        updateRecyclerView(items, loc)
                    } else {
                        Log.e(TAG, "복지센터 응답 실패: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "복지센터 서버 응답 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MentalHealthCenterResponse>, t: Throwable) {
                    Log.e(TAG, "복지센터 통신 실패: ${t.message}")
                    Toast.makeText(requireContext(), "복지센터 API 통신 실패", Toast.LENGTH_SHORT).show()
                }
            })

        } else {
            val call: Call<GgResponse> =
                RetrofitConnection.xmlNetworkService.getMentalCenters(
                    serviceKey = "70442932139549eebb73369c74cd93af",
                    pageNo = 1,
                    numOfRows = 500,
                    type = "xml",
                    sigunName = loc
                )

            call.enqueue(object : Callback<GgResponse> {
                override fun onResponse(call: Call<GgResponse>, response: Response<GgResponse>) {
                    if (response.isSuccessful) {
                        val items = response.body()?.rows ?: emptyList()
                        updateRecyclerView(items, loc)
                    } else {
                        Log.e(TAG, "의료기관 응답 실패: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "의료기관 서버 응답 실패", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<GgResponse>, t: Throwable) {
                    Log.e(TAG, "의료기관 통신 실패: ${t.message}")
                    Toast.makeText(requireContext(), "의료기관 API 통신 실패", Toast.LENGTH_SHORT).show()
                }
            })
        }

        return binding.root
    }

    // 💡 공통 RecyclerView 처리 함수로 분리
    private fun updateRecyclerView(items: List<GgItem>, loc: String) {
        val filteredItems = items.filter {
            it.sigunName?.contains(loc) == true
        }

        if (filteredItems.isEmpty()) {
            Toast.makeText(requireContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.ggRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = CenterRecyclerAdapter(filteredItems) { item ->
                val lat = item.lat?.toDoubleOrNull()
                val lng = item.lng?.toDoubleOrNull()
                if (lat != null && lng != null) {
                    val label = Uri.encode(item.centerName)
                    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(intent)
                    } else {
                        val webUri = Uri.parse("https://maps.google.com/?q=$lat,$lng($label)")
                        startActivity(Intent(Intent.ACTION_VIEW, webUri))
                    }
                } else {
                    Toast.makeText(requireContext(), "위치 정보가 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }
}