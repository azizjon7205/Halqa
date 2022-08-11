package com.example.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.halqa.R
import com.example.halqa.databinding.FragmentHomeBinding
import com.example.utils.Constants.LANGUAGE

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.buttonLotincha.setOnClickListener {

            val bundle = Bundle()
            bundle.putString(LANGUAGE, getString(R.string.language_latin))

            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
        }
        binding.buttonKirilcha.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(LANGUAGE, getString(R.string.language_krill))
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
        }
    }
}