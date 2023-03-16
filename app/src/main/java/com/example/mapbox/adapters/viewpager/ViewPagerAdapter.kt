package com.example.mapbox.adapters.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mapbox.ui.trips.AllTripsFragment
import com.example.mapbox.ui.trips.OneTripsFragment

class ViewPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> {
                AllTripsFragment()
            }
            else -> {
                OneTripsFragment()
            }
        }
    }

}