package com.example.mapbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.mapbox.databinding.ActivityMainBinding
import com.example.mapbox.utils.MySharedPreference
import com.example.mapbox.utils.ThemeHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mySharedPreference: MySharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mySharedPreference = MySharedPreference(this)

        window?.statusBarColor = ContextCompat.getColor(this, R.color.status_color)
        window?.navigationBarColor = ContextCompat.getColor(this, R.color.status_color)

        if(mySharedPreference.getPreferences("isDark") == "1") {
            ThemeHelper.applyTheme(ThemeHelper.darkMode)
        } else {
            ThemeHelper.applyTheme(ThemeHelper.lightMode)
        }
    }
}
