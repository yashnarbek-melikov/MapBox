package com.example.mapbox.utils

import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.compass.compass
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar

fun mapUtils(mapView: MapView) {
    mapView.compass.enabled = false
    mapView.scalebar.enabled = false
    mapView.attribution.enabled = false
    mapView.logo.enabled = false
}