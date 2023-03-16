package com.example.mapbox.ui.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.example.mapbox.R
import com.example.mapbox.adapters.recycler.BounceEdgeEffectFactory
import com.example.mapbox.adapters.recycler.LocationAdapter
import com.example.mapbox.databinding.FragmentOneTripsBinding
import com.example.mapbox.repository.LocationRepository
import com.example.mapbox.room.AppDatabase
import com.example.mapbox.room.entity.LocationEntity
import com.example.mapbox.utils.LocationResource
import com.example.mapbox.utils.MyNavOptions
import com.example.mapbox.viewmodels.LocationViewModel
import com.example.mapbox.viewmodels.LocationViewModelFactory
import kotlinx.coroutines.launch


class OneTripsFragment : Fragment() {

    private var _binding: FragmentOneTripsBinding? = null
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

        locationAdapter = LocationAdapter(object : LocationAdapter.OnClickListener {
            override fun onItemClick(locationEntity: LocationEntity) {
                val bundle = Bundle()
                bundle.putSerializable("key", locationEntity)
                findNavController().navigate(
                    R.id.action_tripsFragment_to_locationFragment,
                    bundle,
                    MyNavOptions.getNavOptions()
                )
            }
        }, requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.edgeEffectFactory = BounceEdgeEffectFactory()
        binding.rv.adapter = locationAdapter
        loadUi()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun loadUi() {
        lifecycleScope.launch {
            locationViewModel.getLocations().collect {
                when (it) {
                    is LocationResource.Loading -> {
                        binding.shimmerLayout.visibility = View.VISIBLE
                        binding.shimmerLayout.startShimmer()
                        binding.rv.visibility = View.GONE
                    }
                    is LocationResource.Error -> {
                    }
                    is LocationResource.Success -> {
                        binding.shimmerLayout.stopShimmer()
                        binding.shimmerLayout.visibility = View.GONE
                        binding.rv.visibility = View.VISIBLE
                        locationAdapter.submitList(it.locationEntities.reversed())
                    }
                }
            }
        }
    }
}