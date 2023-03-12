package com.example.mapbox.ui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentHomeBinding
import com.example.mapbox.utils.LocationPermissionHelper
import com.example.mapbox.utils.MyNavOptions
import com.example.mapbox.utils.MySharedPreference
import com.example.mapbox.utils.ThemeHelper
import com.example.mapbox.workmanager.LocationWorkManager
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.CameraAnimatorOptions.Companion.cameraAnimatorOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.*
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var mySharedPreference: MySharedPreference
    private lateinit var constraints: Constraints
    private lateinit var workRequest: PeriodicWorkRequest

    private var onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center(it).build()
        )
        mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mySharedPreference = MySharedPreference(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView

//        activity?.onBackPressedDispatcher?.addCallback(
//            requireActivity(),
//            object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                        binding.drawerLayout.closeDrawer(GravityCompat.START)
//                    } else {
//                        isEnabled = false
//                        activity?.onBackPressed()
//                    }
//                }
//            })

        mapView.compass.enabled = false
        mapView.scalebar.enabled = false
        mapView.attribution.enabled = false
        mapView.logo.enabled = false

        locationPermissionHelper = LocationPermissionHelper(WeakReference(requireActivity()))

        locationPermissionHelper.checkPermissions {

            constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            workRequest =
                PeriodicWorkRequest.Builder(LocationWorkManager::class.java, 15, TimeUnit.MINUTES)
                    .setConstraints(constraints).build()

            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "LocationWorkManager",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
            onMapReady()
        }

        binding.apply {
            hamburgerId.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.nav_trips -> {
                        findNavController().navigate(
                            R.id.action_homeFragment_to_tripsFragment,
                            null,
                            MyNavOptions.getNavOptions()
                        )
                        binding.drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
                false
            }
            plus.setOnClickListener {
                mapView.camera.apply {
                    val bearing = createBearingAnimator(cameraAnimatorOptions(0.0)) {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                    val zoom =
                        createZoomAnimator(cameraAnimatorOptions(mapView.getMapboxMap().cameraState.zoom + 1.0) {
                            startValue(mapView.getMapboxMap().cameraState.zoom)
                        }) {
                            duration = 100
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                    val pitch = createPitchAnimator(cameraAnimatorOptions(0.0) {
                        startValue(0.0)
                    }) {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                    playAnimatorsSequentially(zoom, pitch, bearing)
                }
            }

            minus.setOnClickListener {

                mapView.camera.apply {
                    val bearing = createBearingAnimator(cameraAnimatorOptions(0.0)) {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                    val zoom =
                        createZoomAnimator(cameraAnimatorOptions(mapView.getMapboxMap().cameraState.zoom - 1.0) {
                            startValue(mapView.getMapboxMap().cameraState.zoom)
                        }) {
                            duration = 100
                            interpolator = AccelerateDecelerateInterpolator()
                        }
                    val pitch = createPitchAnimator(cameraAnimatorOptions(0.0) {
                        startValue(0.0)
                    }) {
                        duration = 100
                        interpolator = AccelerateDecelerateInterpolator()
                    }
                    playAnimatorsSequentially(zoom, pitch, bearing)
                }
            }

            navigator.setOnClickListener {
                val locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
                if (ActivityCompat.checkSelfPermission(
                        requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@setOnClickListener
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
                        Toast.makeText(requireContext(), "Error found", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            lightning.setOnClickListener {
                if (mySharedPreference.getPreferences("isDark") == "1") {
                    ThemeHelper.applyTheme(ThemeHelper.lightMode)
                    mySharedPreference.setPreferences("isDark", "0")
                } else {
                    ThemeHelper.applyTheme(ThemeHelper.darkMode)
                    mySharedPreference.setPreferences("isDark", "1")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
        _binding = null
    }

    @SuppressLint("Lifecycle")
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }
    }

    @SuppressLint("Lifecycle")
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    @SuppressLint("Lifecycle")
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().zoom(14.0).build()
        )
        if (mySharedPreference.getPreferences("isDark") == "1") {
            mapView.getMapboxMap().loadStyleUri(
                Style.DARK
            ) {
                initLocationComponent()
                setupGesturesListener()
            }
        } else {
            mapView.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS
            ) {
                initLocationComponent()
                setupGesturesListener()
            }
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location

        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(requireContext(), R.drawable.taxi),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun onCameraTrackingDismissed() {
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}