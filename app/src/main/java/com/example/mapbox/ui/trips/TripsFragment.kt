package com.example.mapbox.ui.trips

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.example.mapbox.R
import com.example.mapbox.adapters.viewpager.ViewPagerAdapter
import com.example.mapbox.databinding.FragmentTripsBinding
import com.example.mapbox.databinding.ItemTabBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerAdapter = ViewPagerAdapter(this)

        binding.apply {
            viewPager2.adapter = viewPagerAdapter

            backCard.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        tabLayoutMediator()
        tabSelectedListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun tabLayoutMediator() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = getPagerList()[position]

            val itemTabBinding = ItemTabBinding.inflate(layoutInflater)
            itemTabBinding.text.text = tab.text
            if (position == 0) {
                itemTabBinding.linear.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.tab_card_background
                )
                itemTabBinding.text.setTextColor(Color.BLACK)
            } else {
                itemTabBinding.text.setTextColor(Color.BLACK)
                itemTabBinding.linear.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.tab_unselected_color
                )
            }
            tab.customView = itemTabBinding.root
        }.attach()
    }

    private fun tabSelectedListener() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val itemTabBinding = ItemTabBinding.bind(tab?.customView!!)
                itemTabBinding.linear.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.tab_card_background
                )
                itemTabBinding.text.setTextColor(Color.BLACK)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val itemTabBinding = ItemTabBinding.bind(tab?.customView!!)
                itemTabBinding.text.setTextColor(Color.BLACK)
                itemTabBinding.linear.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.tab_unselected_color
                )
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })
    }
    private fun getPagerList(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("One trip")
        list.add("All trips")
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}