package com.example.mapbox.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.mapbox.room.entity.LocationEntity

@Dao
interface LocationDao {

    @Insert(onConflict = REPLACE)
    suspend fun addLocationEntity(locationEntity: LocationEntity)

    @Query("select * from locationEntity")
    suspend fun getLocationEntities(): List<LocationEntity>
}