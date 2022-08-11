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
import com.example.utils.Constants
import com.example.utils.Constants.BOOK
import com.example.utils.Constants.JANGCHI
import com.example.utils.Constants.KEY
import com.example.utils.Constants.LANGUAGE
import com.example.utils.SharedPref

class BookFragment : Fragment(R.layout.fragment_book) {

    private val binding by viewBinding(FragmentBookBinding::bind)
    private lateinit var language: String
    private lateinit var book: String
    private lateinit var adapter: MenuAdapter

    private lateinit var sharedPref: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            language = it.getString(LANGUAGE)!!
            book = it.getString(BOOK)!!
        }

        sharedPref = SharedPref(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {


        binding.apply {
            viewPager.adapter = getRequiredAdapter()

            viewPager.setCurrentItem(sharedPref.getLastPageNumber(KEY), false)

            ivMenu.setOnClickListener {
                drawerLayout.openDrawer(GravityCompat.END, true)
            }

            adapter = MenuAdapter { position ->
                drawerLayout.closeDrawer(GravityCompat.END, true)

                viewPager.setCurrentItem(position, false)

                sharedPref.saveLastPageNumber(KEY, viewPager.currentItem)
            }

            adapter.submitList(getRequiredChapterAdapter())

            recyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = adapter

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun getRequiredChapterAdapter(): List<String> {
        if (language == getString(R.string.language_latin)) {
            //latin
            return if (book == JANGCHI) {
                //jangchi
                resources.getStringArray(R.array.chapters_jangchi_latin).toList()
            } else {
                //Halqa
                resources.getStringArray(R.array.chapters_halqa_latin).toList()
            }
        } else {
            return if (book == JANGCHI) {
                //jangchi
                resources.getStringArray(R.array.chapter_jangchi_crill).toList()
            } else {
                //Halqa
                resources.getStringArray(R.array.chapters_halqa_crill).toList()
            }
        }
    }

    private fun getRequiredAdapter(): BookAdapter {
        if (language == getString(R.string.language_latin)) {
            //latin
            return if (book == JANGCHI) {
                //jangchi
                BookAdapter(
                    resources.getStringArray(R.array.chapters_jangchi_latin).toList(),
                    resources.getStringArray(R.array.text_of_chapters_jangchi_latin).toList()
                )
            } else {
                //Halqa
                BookAdapter(
                    resources.getStringArray(R.array.chapters_halqa_latin).toList(),
                    resources.getStringArray(R.array.text_of_chapters_halqa_latin).toList()
                )
            }
        } else {
            return if (book == JANGCHI) {
                //jangchi
                BookAdapter(
                    resources.getStringArray(R.array.chapter_jangchi_crill).toList(),
                    resources.getStringArray(R.array.text_of_chapters_jangchi_crill).toList()
                )
            } else {
                //Halqa
                BookAdapter(
                    resources.getStringArray(R.array.chapters_halqa_crill).toList(),
                    resources.getStringArray(R.array.text_of_chapters_halqa_crill).toList()
                )
            }
        }
    }
}