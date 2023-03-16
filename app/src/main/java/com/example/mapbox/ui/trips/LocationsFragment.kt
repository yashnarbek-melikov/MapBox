package com.example.mapbox.ui.trips

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentLocationsBinding
import com.example.mapbox.repository.LocationRepository
import com.example.mapbox.room.AppDatabase
import com.example.mapbox.utils.*
import com.example.mapbox.viewmodels.LocationViewModel
import com.example.mapbox.viewmodels.LocationViewModelFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.launch

class LocationsFragment : Fragment() {

    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var mySharedPreference: MySharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mySharedPreference = MySharedPreference(requireContext())

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModelFactory(
                LocationRepository(
                    AppDatabase.getInstance(requireContext()).locationDao()
                )
            )
        )[LocationViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView

        mapUtils(mapView)

        binding.apply {
            backCard.setOnClickListener {
                findNavController().popBackStack()
            }
            plus.setOnClickListener {
                plusSetOnClickListener(mapView)
            }
            minus.setOnClickListener {
                minusSetOnClickListener(mapView)
            }
        }

        mapView.getMapboxMap().flyTo(cameraOptions {
            center(
                Point.fromLngLat(
                    69.2495591,
                    41.2970818
                )
            ).zoom(10.0)
        }, MapAnimationOptions.mapAnimationOptions { duration(2000) })

        if (mySharedPreference.getPreferences("isDark") == "1") {
            mapView.getMapboxMap().loadStyleUri(
                Style.DARK
            ) {
                loadUi()
            }
        } else {
            mapView.getMapboxMap().loadStyleUri(
                Style.MAPBOX_STREETS
            ) {
                loadUi()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun addAnnotationToMap(longitude: Double?, latitude: Double?) {

        bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(
                    Point.fromLngLat(
                        longitude ?: 69.2495591,
                        latitude ?: 41.2970818
                    )
                )
                .withIconImage(it)
            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    private fun loadUi() {
        lifecycleScope.launch {
            locationViewModel.getLocations().collect {
                when (it) {
                    is LocationResource.Loading -> {

                    }
                    is LocationResource.Error -> {
                    }
                    is LocationResource.Success -> {
                        it.locationEntities.forEach { location ->
                            addAnnotationToMap(location.longitude, location.latitude)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}