package com.example.mapbox.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mapbox.databinding.ItemLocationBinding
import com.example.mapbox.room.entity.LocationEntity

class LocationAdapter : ListAdapter<LocationEntity, LocationAdapter.Vh>(MyDiffUtill()) {

    inner class Vh(var itemLocationBinding: ItemLocationBinding) :
        RecyclerView.ViewHolder(itemLocationBinding.root) {

        fun onBind(locationEntity: LocationEntity) {
            itemLocationBinding.apply {
                latitude.text = locationEntity.latitude.toString()
                longitude.text = locationEntity.longitude.toString()
                date.text =
                    "Date: ${locationEntity.date.date} - Hours: ${locationEntity.date.hours} - Minutes: ${locationEntity.date.minutes} - Seconds: ${locationEntity.date.seconds}"
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
}