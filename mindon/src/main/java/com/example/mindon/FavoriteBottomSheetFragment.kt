package com.example.mindon

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mindon.databinding.FragmentFavoriteBinding
import com.example.mindon.models.CenterInfo
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteBottomSheetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var adapter: CenterAdapter
    private val favoriteList = mutableListOf<CenterInfo>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        loadFavorites()
        return binding.root
    }

    private fun loadFavorites() {
        val dao = FavoriteDao(DBHelper(requireContext()).writableDatabase)
        favoriteList.clear()
        favoriteList.addAll(dao.getAllFavorites())

        if (!::adapter.isInitialized) {
            adapter = CenterAdapter(requireContext(), favoriteList) {
                loadFavorites()  // 즐겨찾기 삭제 시 새로고침
            }
            binding.recyclerViewFavorites.adapter = adapter
            binding.recyclerViewFavorites.layoutManager = LinearLayoutManager(requireContext())
        } else {
            adapter.notifyDataSetChanged()
        }

        Log.d("즐겨찾기", "불러온 개수: ${favoriteList.size}")
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }


}