package com.example.mapbox.repository

import com.example.mapbox.room.dao.LocationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LocationRepository(private val locationDao: LocationDao) {

    suspend fun getDbLocations() = flow { emit(locationDao.getLocationEntities()) }.flowOn(Dispatchers.IO)
}