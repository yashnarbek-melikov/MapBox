package com.example.mapbox.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.*
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentHomeBinding
import com.example.mapbox.utils.*
import com.example.mapbox.utils.RequestCode.Companion.MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
import com.example.mapbox.utils.RequestCode.Companion.MY_PERMISSIONS_REQUEST_LOCATION
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mySharedPreference: MySharedPreference

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

        activityOnBackPressed()
        mapUtils(mapView)

        binding.apply {
            hamburgerId.setOnClickListener {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
            drawerNavigationSelected(navView)

            plus.setOnClickListener {
                plusSetOnClickListener(mapView)
            }

            minus.setOnClickListener {
                minusSetOnClickListener(mapView)
            }

            navigator.setOnClickListener {
                navigationSetOnClickListener(requireContext(), mapView)
            }

            lightning.setOnClickListener {
                lightningSetOnClickListener(mySharedPreference)
            }

            notificationsCard.setOnClickListener {
                Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT).show()
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

    override fun onResume() {
        super.onResume()
        checkLocationPermission(requireContext(), requireActivity()).apply {
            if (this) {
                onMapReady()
                workManagerWorking(requireContext())
            }
        }
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

    private fun onCameraTrackingDismissed() {
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        checkBackgroundLocation(requireContext(), requireActivity())
                    }

                } else {
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null),
                            ),
                        )
                    }
                }
                return
            }
            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            requireContext(),
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    private fun activityOnBackPressed() {
        activity?.onBackPressedDispatcher?.addCallback(
            requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (this@HomeFragment.isVisible) {
                        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            binding.drawerLayout.closeDrawer(GravityCompat.START)
                        } else {
                            isEnabled = false
                            activity?.finish()
                        }
                    } else {
                        findNavController().popBackStack()
                    }
                }
            })
    }
}