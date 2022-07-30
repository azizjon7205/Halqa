package com.example.fragment

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.halqa.R
import com.example.halqa.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment(R.layout.fragment_category) {
    private val binding by viewBinding(FragmentCategoryBinding::bind)
    lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            language = it.getString("language")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        when (language) {
            getString(R.string.language_latin) -> {
                binding.ivBook1.setImageResource(R.drawable.im_halqa_pdf_latin)
                binding.ivBook2.setImageResource(R.drawable.im_jangchi_pdf_latin)
                binding.ivBook3.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook4.setImageResource(R.drawable.im_jangchi_audio_1_latin)
                binding.ivBook5.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook6.setImageResource(R.drawable.im_jangchi_audio_2_latin)

                binding.mcv1.setOnClickListener {

                    findNavController().navigate(
                        R.id.action_categoryFragment_to_bookFragment,
                        bundleOf("language" to getString(R.string.language_latin))
                    )
                }
            }
            getString(R.string.language_krill) -> {
                binding.ivBook1.setImageResource(R.drawable.im_halqa_pdf_krill)
                binding.ivBook2.setImageResource(R.drawable.im_jangchi_pdf_krill)
                binding.ivBook3.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook4.setImageResource(R.drawable.im_jangchi_audio_1_latin)
                binding.ivBook5.setImageResource(R.drawable.im_halqa_audio_1_latin)
                binding.ivBook6.setImageResource(R.drawable.im_jangchi_audio_2_latin)

                binding.mcv1.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_categoryFragment_to_bookFragment,
                        bundleOf("language" to getString(R.string.language_krill))
                    )
                }
            }
        }

        binding.ivBook3.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_halqaAudioFragment)
        }
    }
}