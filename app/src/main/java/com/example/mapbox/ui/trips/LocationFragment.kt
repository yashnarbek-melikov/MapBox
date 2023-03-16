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
import androidx.navigation.fragment.findNavController
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentLocationBinding
import com.example.mapbox.room.entity.LocationEntity
import com.example.mapbox.utils.mapUtils
import com.example.mapbox.utils.minusSetOnClickListener
import com.example.mapbox.utils.plusSetOnClickListener
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

private const val ARG_PARAM1 = "key"

class LocationFragment : Fragment() {
    private var param1: LocationEntity? = null
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as LocationEntity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapView

        mapUtils(mapView)

        binding.apply {
            backCard.setOnClickListener {
                findNavController().popBackStack()
            }
            navigator.setOnClickListener {
                mapView.getMapboxMap().flyTo(cameraOptions {
                    center(
                        Point.fromLngLat(
                            param1?.longitude ?: 41.2970818, param1?.latitude ?: 69.2495591
                        )
                    ).zoom(14.0)
                }, MapAnimationOptions.mapAnimationOptions { duration(2000) })
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
                    param1?.longitude ?: 41.2970818,
                    param1?.latitude ?: 69.2495591
                )
            ).zoom(14.0)
        }, MapAnimationOptions.mapAnimationOptions { duration(2000) })

        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) { addAnnotationToMap() }
    }

    private fun addAnnotationToMap() {

        bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )?.let {
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager(mapView)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(
                    Point.fromLngLat(
                        param1?.longitude ?: 69.2495591,
                        param1?.latitude ?: 41.2970818
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}