package com.example.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.halqa.R
import com.example.halqa.databinding.FragmentBookBinding
import com.example.halqa.databinding.FragmentCategoryBinding

class BookFragment : Fragment(R.layout.fragment_book) {
    private val binding by viewBinding(FragmentBookBinding::bind)
    lateinit var language:String

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
                binding.pdfbook.fromAsset("Halqa.pdf").load()
            }
            getString(R.string.language_krill) -> {
                binding.pdfbook.fromAsset("ҲАЛҚА.pdf").load()
            }

        }
    }
}