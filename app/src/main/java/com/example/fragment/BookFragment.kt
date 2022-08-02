package com.example.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.BookAdapter
import com.example.adapter.MenuAdapter
import com.example.halqa.R
import com.example.halqa.databinding.FragmentBookBinding
import com.example.utils.Constants.KEY
import com.example.utils.SharedPref

class BookFragment : Fragment(R.layout.fragment_book) {
    private val binding by viewBinding(FragmentBookBinding::bind)
    lateinit var language: String
    private lateinit var adapter: MenuAdapter
    private lateinit var bookAdapter: BookAdapter

    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            language = it.getString("language")!!
        }

        sharedPref = SharedPref(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

        bookAdapter = BookAdapter(
            resources.getStringArray(R.array.chapters_halqa_latin).toList(),
            resources.getStringArray(R.array.text_of_chapters_halqa_latin).toList()
        )

        binding.apply {
            viewPager.adapter = bookAdapter

            viewPager.setCurrentItem(sharedPref.getLastPageNumber(KEY), false)

            ivMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.END, true)
            }

            adapter = MenuAdapter { position ->
                drawerLayout.closeDrawer(GravityCompat.END, true)

                viewPager.setCurrentItem(position, false)

                sharedPref.saveLastPageNumber(KEY, viewPager.currentItem)
            }

            adapter.submitList(resources.getStringArray(R.array.chapters_halqa_latin).toList())

            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }
}