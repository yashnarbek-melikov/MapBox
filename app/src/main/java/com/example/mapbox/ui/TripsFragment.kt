package com.example.mapbox.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mapbox.adapters.LocationAdapter
import com.example.mapbox.databinding.FragmentTripsBinding
import com.example.mapbox.repository.LocationRepository
import com.example.mapbox.room.AppDatabase
import com.example.mapbox.utils.LocationResource
import com.example.mapbox.viewmodels.LocationViewModel
import com.example.mapbox.viewmodels.LocationViewModelFactory
import kotlinx.coroutines.launch


class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var locationViewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModelFactory(
                LocationRepository(
                    AppDatabase.getInstance(requireContext()).locationDao()
                )
            )
        )[LocationViewModel::class.java]

        locationAdapter = LocationAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rv.adapter = locationAdapter
        loadUi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun loadUi() {
        lifecycleScope.launch {
            locationViewModel.getLocations().collect {
                when (it) {
                    is LocationResource.Loading -> {

                    }
                    is LocationResource.Error -> {
                    }
                    is LocationResource.Success -> {
                        locationAdapter.submitList(it.locationEntities)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}