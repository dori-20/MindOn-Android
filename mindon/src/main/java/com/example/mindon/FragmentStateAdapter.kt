package com.example.mindon

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MyFragmentStateAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 5 // 프래그먼트 개수 명시적 선언

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> EmotionFragment()
            2 -> CenterFragment()
            3 -> CommunityFragment()
            4 -> SettingsFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}
