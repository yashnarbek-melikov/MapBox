package com.example.mapbox.utils

import com.example.mapbox.room.entity.LocationEntity

sealed class LocationResource {

    object Loading: LocationResource()

    data class Success(val locationEntities: List<LocationEntity>): LocationResource()

    data class Error(val message: String): LocationResource()
}