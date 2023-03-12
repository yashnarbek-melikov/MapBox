package com.example.mapbox.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapbox.repository.LocationRepository
import com.example.mapbox.utils.LocationResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {

    fun getLocations(): StateFlow<LocationResource> {
        val stateFlow = MutableStateFlow<LocationResource>(LocationResource.Loading)

        viewModelScope.launch {
            locationRepository.getDbLocations().catch {
                stateFlow.emit(LocationResource.Error(it.message ?: ""))
            }.collect {
                stateFlow.emit(LocationResource.Success(it))
            }
        }
        return stateFlow
    }
}