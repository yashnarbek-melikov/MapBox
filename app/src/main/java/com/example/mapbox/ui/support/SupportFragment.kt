package com.example.mapbox.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mapbox.databinding.FragmentSupportBinding

class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            smsCard.setOnClickListener {
                Toast.makeText(requireContext(), "Support clicked!", Toast.LENGTH_SHORT).show()
            }
            callCard.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+998906772149"))
                startActivity(intent)
            }
            telegramCard.setOnClickListener {
                val telegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/yashnarbek_melikov"))
                startActivity(telegram)
            }
            reportCard.setOnClickListener {
                Toast.makeText(requireContext(), "Report clicked!", Toast.LENGTH_SHORT).show()
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
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}