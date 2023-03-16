package com.example.mapbox.utils

import android.content.Context
import android.content.pm.PackageManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.example.mapbox.R
import com.example.mapbox.workmanager.LocationWorkManager
import com.google.android.material.navigation.NavigationView
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.flyTo
import java.util.concurrent.TimeUnit

fun Fragment.drawerNavigationSelected(navigationView: NavigationView) {
    navigationView.setNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.nav_trips -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_tripsFragment
                )
            }
            R.id.nav_payment -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_paymentFragment
                )
            }
            R.id.nav_favorite -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_favoriteAdressesFragment
                )
            }
            R.id.nav_promo -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_promoCodeFragment
                )
            }
            R.id.nav_notifications -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_notificationsFragment
                )
            }
            R.id.nav_support -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_supportFragment
                )
            }
            R.id.nav_settings -> {
                findNavController().navigate(
                    R.id.action_homeFragment_to_settingsFragment
                )
            }
        }
        false
    }
}

fun plusSetOnClickListener(mapView: MapView) {
    mapView.camera.apply {
        val bearing = createBearingAnimator(CameraAnimatorOptions.cameraAnimatorOptions(0.0)) {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }
        val zoom =
            createZoomAnimator(CameraAnimatorOptions.cameraAnimatorOptions(mapView.getMapboxMap().cameraState.zoom + 1.0) {
                startValue(mapView.getMapboxMap().cameraState.zoom)
            }) {
                duration = 100
                interpolator = AccelerateDecelerateInterpolator()
            }
        val pitch = createPitchAnimator(CameraAnimatorOptions.cameraAnimatorOptions(0.0) {
            startValue(0.0)
        }) {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }
        playAnimatorsSequentially(zoom, pitch, bearing)
    }
}

fun minusSetOnClickListener(mapView: MapView) {
    mapView.camera.apply {
        val bearing = createBearingAnimator(CameraAnimatorOptions.cameraAnimatorOptions(0.0)) {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }
        val zoom =
            createZoomAnimator(CameraAnimatorOptions.cameraAnimatorOptions(mapView.getMapboxMap().cameraState.zoom - 1.0) {
                startValue(mapView.getMapboxMap().cameraState.zoom)
            }) {
                duration = 100
                interpolator = AccelerateDecelerateInterpolator()
            }
        val pitch = createPitchAnimator(CameraAnimatorOptions.cameraAnimatorOptions(0.0) {
            startValue(0.0)
        }) {
            duration = 100
            interpolator = AccelerateDecelerateInterpolator()
        }
        playAnimatorsSequentially(zoom, pitch, bearing)
    }
}

fun navigationSetOnClickListener(context: Context, mapView: MapView) {
    val locationEngine = LocationEngineProvider.getBestLocationEngine(context)
    if (ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    locationEngine.getLastLocation(object :
        LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            mapView.getMapboxMap().flyTo(cameraOptions {
                center(result?.lastLocation?.longitude?.let { it1 ->
                    result.lastLocation?.latitude?.let { it2 ->
                        Point.fromLngLat(
                            it1, it2
                        )
                    }
                }).zoom(14.0)
            }, MapAnimationOptions.mapAnimationOptions { duration(2000) })
        }

        override fun onFailure(exception: Exception) {
            Toast.makeText(context, "Error found", Toast.LENGTH_SHORT).show()
        }
    })
}

fun lightningSetOnClickListener(mySharedPreference: MySharedPreference) {
    if (mySharedPreference.getPreferences("isDark") == "1") {
        ThemeHelper.applyTheme(ThemeHelper.lightMode)
        mySharedPreference.setPreferences("isDark", "0")
    } else {
        ThemeHelper.applyTheme(ThemeHelper.darkMode)
        mySharedPreference.setPreferences("isDark", "1")
    }
}

fun workManagerWorking(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest =
        PeriodicWorkRequest.Builder(
            LocationWorkManager::class.java,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints).build()

    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "LocationWorkManager",
        ExistingPeriodicWorkPolicy.KEEP,
        workRequest
    )
}