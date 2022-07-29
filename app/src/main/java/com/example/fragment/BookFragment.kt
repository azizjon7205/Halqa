package com.example.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.MenuAdapter
import com.example.halqa.R
import com.example.halqa.databinding.FragmentBookBinding


class BookFragment : Fragment(R.layout.fragment_book) {
    private val binding by viewBinding(FragmentBookBinding::bind)
    lateinit var language:String
    private lateinit var adapter: MenuAdapter

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



        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        adapter = MenuAdapter {

        }

        adapter.submitList(arrayListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

//        when (language) {
//            getString(R.string.language_latin) -> {
//                binding.pdfbook.fromAsset("Halqa.pdf").load()
//            }
//            getString(R.string.language_krill) -> {
//                binding.pdfbook.fromAsset("ҲАЛҚА.pdf").load()
//            }
//
//        }
    }

}