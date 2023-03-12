package com.example.mapbox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mapbox.repository.LocationRepository

class LocationViewModelFactory(private val locationRepository: LocationRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            return LocationViewModel(locationRepository) as T
        }
        throw Exception("Error")
    }
}