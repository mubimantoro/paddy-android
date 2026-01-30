package com.example.sipaddy.presentation.history

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sipaddy.presentation.pengaduantanaman.history.HistoryPengaduanTanamanFragment

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryPredictionFragment()
            1 -> HistoryPengaduanTanamanFragment()
            else -> HistoryPredictionFragment()
        }
    }

    override fun getItemCount(): Int = 2
}