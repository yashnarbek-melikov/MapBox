package com.example.mapbox.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentModeBinding
import com.example.mapbox.utils.MySharedPreference
import com.example.mapbox.utils.ThemeHelper

class ModeFragment : Fragment() {

    private var _binding: FragmentModeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mySharedPreference: MySharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mySharedPreference = MySharedPreference(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            backCard.setOnClickListener {
                findNavController().popBackStack()
            }
            switchLayout.isChecked = mySharedPreference.getPreferences("isDark") == "1"
            if (switchLayout.isChecked) {
                darkLightText.text = getString(R.string.light_mode)
            } else {
                darkLightText.text = getString(R.string.dark_mode)
            }
            switchLayout.setOnCheckedChangeListener { _, b ->
                if (b) {
                    ThemeHelper.applyTheme(ThemeHelper.darkMode)
                    mySharedPreference.setPreferences("isDark", "1")
                } else {
                    ThemeHelper.applyTheme(ThemeHelper.lightMode)
                    mySharedPreference.setPreferences("isDark", "0")
                }
            }
            card1.setOnClickListener {
                binding.switchLayout.isChecked = binding.switchLayout.isChecked != true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentModeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}