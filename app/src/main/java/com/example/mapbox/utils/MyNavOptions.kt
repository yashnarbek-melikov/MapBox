package com.example.mapbox.utils

import androidx.navigation.NavOptions
import com.example.mapbox.R


class MyNavOptions {
    companion object {
        fun getNavOptions(): NavOptions {
            return NavOptions.Builder()
                .setEnterAnim(R.anim.enter)
                .setExitAnim(R.anim.exit)
                .setPopEnterAnim(R.anim.pop_enter)
                .setPopExitAnim(R.anim.pop_exit).build()
        }
    }
}