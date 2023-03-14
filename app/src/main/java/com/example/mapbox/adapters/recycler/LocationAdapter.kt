package com.example.mapbox.adapters.recycler

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mapbox.databinding.ItemLocationBinding
import com.example.mapbox.room.entity.LocationEntity
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LocationAdapter(val listener: OnClickListener, val context: Context) :
    ListAdapter<LocationEntity, LocationAdapter.Vh>(
        MyDiffUtill()
    ) {

    inner class Vh(var itemLocationBinding: ItemLocationBinding) :
        RecyclerView.ViewHolder(itemLocationBinding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(locationEntity: LocationEntity) {

            itemLocationBinding.apply {
                place.text = geoCoder(locationEntity.latitude, locationEntity.longitude)
                time.text = simpleTimeFormat(locationEntity.calendar)
                date.text = simpleDateFormat(locationEntity.calendar)
            }
            itemLocationBinding.itemView.setOnClickListener {
                listener.onItemClick(locationEntity)
            }
        }
    }

    class MyDiffUtill : DiffUtil.ItemCallback<LocationEntity>() {
        override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position))
    }

    interface OnClickListener {
        fun onItemClick(locationEntity: LocationEntity)
    }

    fun simpleTimeFormat(calendar: Calendar): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }
    fun simpleDateFormat(calendar: Calendar): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun geoCoder(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var location = ""
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            val obj: Address = addresses!![0]

            if (obj.subLocality != null) {
                location += obj.subLocality
            }
            if (obj.locality != null) {
                location += ", " + obj.locality
            }
            if (obj.adminArea != null) {
                location += ", " + obj.adminArea
            }
            if (obj.countryName != null) {
                location += ", " + obj.countryName
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return location
    }
}