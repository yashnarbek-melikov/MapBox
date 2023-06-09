package com.example.mapbox.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mapbox.room.AppDatabase
import com.example.mapbox.room.entity.LocationEntity
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import java.util.*

class LocationWorkManager(var context: Context, workParameters: WorkerParameters) :
    Worker(context, workParameters) {

    private lateinit var appDatabase: AppDatabase

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        appDatabase = AppDatabase.getInstance(context)

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (!isGpsEnabled && !isNetworkEnabled) {

        }

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            GlobalScope.launch {
                if (location != null) {
                    appDatabase.locationDao().addLocationEntity(
                        LocationEntity(
                            longitude = location.longitude,
                            latitude = location.latitude,
                            calendar = Calendar.getInstance()
                        )
                    )
                } else {
                    appDatabase.locationDao().addLocationEntity(
                        LocationEntity(
                            longitude = 41.2970818,
                            latitude = 69.2495591,
                            calendar = Calendar.getInstance()
                        )
                    )
                }
            }
        }

        return Result.success()
    }
}