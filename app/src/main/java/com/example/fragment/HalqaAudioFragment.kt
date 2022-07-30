package com.example.fragment

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.AudioBookAdapter
import com.example.database.AppDatabase
import com.example.halqa.R
import com.example.halqa.databinding.FragmentHalqaAudioBinding
import com.example.helper.OnItemClickListner
import com.example.model.Halqa
import com.example.utils.Constants.HALQA


class HalqaAudioFragment : Fragment(R.layout.fragment_halqa_audio) {
    private val binding by viewBinding(FragmentHalqaAudioBinding::bind)
    private lateinit var appDatabase: AppDatabase
    private lateinit var book: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            book = it.getString("audio")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        initViews()
    }

    private fun initViews() {
        refreshAdapter()
    }

    fun refreshAdapter() {
        val items = appDatabase.halqaDao().getPosts(HALQA) as ArrayList<Halqa>
        val adapter = AudioBookAdapter(this, items, object : OnItemClickListner {
            override fun onItemDownload(halqa: Halqa) {
                downloadFile(halqa)
            }

            override fun onItemPlay(fileName: String, audioName: String) {

            }


        })
        binding.recyclerView.adapter = adapter
    }

    fun downloadFile(halqa: Halqa) {
        val folderName = "HalqaKitob/${halqa.bookName}"
        val request = DownloadManager.Request(Uri.parse(halqa.url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("Halqa")
            .setDescription("Halqa Audio Kitob")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalFilesDir(
                context,
                folderName,
                "${halqa.bookName}${halqa.bob}.mp3"
            )
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
        // refreshAdapter(items)
        appDatabase.halqaDao().updatePost(true, halqa.id!!)
    }
}