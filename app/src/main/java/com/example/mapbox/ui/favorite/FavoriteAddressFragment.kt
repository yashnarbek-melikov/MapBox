package com.example.mapbox.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mapbox.databinding.FragmentFavoriteAdressesBinding

class FavoriteAddressFragment : Fragment() {

    private var _binding: FragmentFavoriteAdressesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            homeCard.setOnClickListener {
                Toast.makeText(requireContext(), "Home clicked!", Toast.LENGTH_SHORT).show()
            }
            workCard.setOnClickListener {
                Toast.makeText(requireContext(), "Work clicked!", Toast.LENGTH_SHORT).show()
            }
            newCard.setOnClickListener {
                Toast.makeText(requireContext(), "New addresses clicked!", Toast.LENGTH_SHORT).show()
            }
            backCard.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteAdressesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}