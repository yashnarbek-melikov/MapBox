package com.example.mapbox.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mapbox.R
import com.example.mapbox.databinding.FragmentLanguageBinding
import com.example.mapbox.utils.hide
import com.example.mapbox.utils.show
import com.yariksoffice.lingver.Lingver

class LanguageFragment : Fragment() {

    private var _binding: FragmentLanguageBinding? = null
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
            var language = Lingver.getInstance().getLanguage()

            binding.apply {

                fun select(card: CardView, text: TextView, image: ImageView) {
                    titleId.text = getString(R.string.language)
                    text1.text = getString(R.string.russian)
                    text2.text = getString(R.string.uzbek)
                    text3.text = getString(R.string.english)

                    card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selected))
                    text.setTextColor(ContextCompat.getColor(requireContext(), R.color.dark_white))
                    image.show()
                }

                when (language) {
                    "ru" -> {
                        select(card1, text1, img1)
                    }
                    "en" -> {
                        select(card3, text3, img3)
                    }
                    else -> {
                        select(card2, text2, img2)
                    }
                }

                fun clear() {
                    val color1 = ContextCompat.getColor(requireContext(), R.color.card_dark_light)
                    card1.setCardBackgroundColor(color1)
                    card2.setCardBackgroundColor(color1)
                    card3.setCardBackgroundColor(color1)
                    val color2 = ContextCompat.getColor(requireContext(), R.color.black)
                    text1.setTextColor(color2)
                    text2.setTextColor(color2)
                    text3.setTextColor(color2)

                    img1.hide()
                    img2.hide()
                    img3.hide()
                }

                card1.setOnClickListener {
                    if (language != "ru") {
                        Lingver.getInstance().setLocale(requireContext(), "ru")
                        language = "ru"
                        clear()
                        select(card1, text1, img1)
                    }
                }
                card3.setOnClickListener {
                    if (language != "en") {
                        language = "en"
                        Lingver.getInstance().setLocale(requireContext(), "en")
                        clear()
                        select(card3, text3, img3)
                    }
                }
                card2.setOnClickListener {
                    if (language != "uz") {
                        language = "uz"
                        Lingver.getInstance().setLocale(requireContext(), "uz")
                        clear()
                        select(card2, text2, img2)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLanguageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}