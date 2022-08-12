package com.example.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.AudioBookAdapter2
import com.example.database.AppDatabase
import com.example.halqa.R
import com.example.halqa.databinding.FragmentAllAudioListBinding
import com.example.model.BookData
import com.example.utils.Constants.AUDIO
import com.example.utils.UiStateList
import com.example.viewmodel.AllAudioViewModel
import kotlinx.coroutines.launch

class AllAudioListFragment : Fragment(R.layout.fragment_all_audio_list) {

    private lateinit var allAudioViewModel: AllAudioViewModel
    private var appDatabase: AppDatabase? = null
    private lateinit var bookName: String
    private val binding by viewBinding(FragmentAllAudioListBinding::bind)
    private lateinit var audioBookAdapter: AudioBookAdapter2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        allAudioViewModel = AllAudioViewModel(appDatabase!!.bookDao())
        arguments?.let {
            bookName = it.getString(AUDIO)!!
        }
        sendRequestToGetAudioList()
    }

    private fun sendRequestToGetAudioList() {
        allAudioViewModel.getBookAudios(bookName)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObserver()
    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                allAudioViewModel.allBookAudios.collect {
                    when (it) {
                        UiStateList.LOADING -> {
                            //show progress
                        }

                        is UiStateList.SUCCESS -> {
                            refreshAdapter(it.data)
                        }
                        is UiStateList.ERROR -> {
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun refreshAdapter(audioList: List<BookData>) {
        Log.d("TAG", "refreshAdapter: $bookName")
        Log.d("TAG", "refreshAdapter: $audioList")
        audioBookAdapter = AudioBookAdapter2 { audio ->
            findNavController().navigate(
                R.id.action_allAudioListFragment_to_audioPlayFragment,
                bundleOf(AUDIO to audio.id)
            )
        }
        audioBookAdapter.submitList(audioList)
        binding.recyclerView.adapter = audioBookAdapter
    }
}