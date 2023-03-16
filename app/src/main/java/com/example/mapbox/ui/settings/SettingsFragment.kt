package com.example.mapbox.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentSettingsBinding
import com.example.mapbox.utils.MyNavOptions

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            backCard.setOnClickListener {
                findNavController().popBackStack()
            }

            languageLayout.setOnClickListener {
                findNavController().navigate(
                    R.id.action_settingsFragment_to_languageFragment,
                    null,
                    MyNavOptions.getNavOptions()
                )
            }
            mapTypeLayout.setOnClickListener {
                findNavController().navigate(
                    R.id.action_settingsFragment_to_mapTypeFragment,
                    null,
                    MyNavOptions.getNavOptions()
                )
            }
            modeLayout.setOnClickListener {
                findNavController().navigate(
                    R.id.action_settingsFragment_to_modeFragment,
                    null,
                    MyNavOptions.getNavOptions()
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}