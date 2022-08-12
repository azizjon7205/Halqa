package com.example.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.halqa.R
import com.example.halqa.databinding.FragmentCategoryBinding
import com.example.utils.Constants.AUDIO
import com.example.utils.Constants.BOOK
import com.example.utils.Constants.HALQA
import com.example.utils.Constants.JANGCHI
import com.example.utils.Constants.LANGUAGE

class CategoryFragment : Fragment(R.layout.fragment_category) {
    private val binding by viewBinding(FragmentCategoryBinding::bind)
    lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            language = it.getString(LANGUAGE)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        if (language == getString(R.string.language_krill)) {
            binding.ivBookHalqa.setImageResource(R.drawable.im_halqa_pdf_krill)
            binding.ivBookJangchi.setImageResource(R.drawable.im_jangchi_pdf_krill)
        }

        binding.apply {
            halqaWrapper.setOnClickListener {
                findNavController().navigate(
                    R.id.action_categoryFragment_to_bookFragment,
                    bundleOf(LANGUAGE to language, BOOK to HALQA)
                )
            }

            jangchiWrapper.setOnClickListener {
                findNavController().navigate(
                    R.id.action_categoryFragment_to_bookFragment,
                    bundleOf(LANGUAGE to language, BOOK to JANGCHI)
                )
            }

            halqaAudioAbdukarimMWrapper.setOnClickListener {
                findNavController().navigate(
                    R.id.action_categoryFragment_to_allAudioListFragment,
                    bundleOf(AUDIO to HALQA)
                )
            }

            jangchiAudioAbdukarimMWrapper.setOnClickListener {
                findNavController().navigate(
                    R.id.action_categoryFragment_to_allAudioListFragment,
                    bundleOf(AUDIO to JANGCHI)
                )
            }
        }
    }
}