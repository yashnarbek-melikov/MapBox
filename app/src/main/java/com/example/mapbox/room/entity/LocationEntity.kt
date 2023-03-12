package com.example.mapbox.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val longitude: Double,
    val latitude: Double,
    val date: Date
)