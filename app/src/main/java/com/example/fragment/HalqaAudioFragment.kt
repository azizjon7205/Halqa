package com.example.fragment

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.AudioBookAdapter
import com.example.halqa.R
import com.example.halqa.databinding.FragmentHalqaAudioBinding
import com.example.model.Halqa
import java.io.File


class HalqaAudioFragment : Fragment(R.layout.fragment_halqa_audio) {
    private val binding by viewBinding(FragmentHalqaAudioBinding::bind)
    private var items = ArrayList<Halqa>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        refreshAdapter(itemsList())
    }

    fun refreshAdapter(items: ArrayList<Halqa>) {
        val adapter = AudioBookAdapter(this, items){
            downloadFile(it)
        }
        binding.recyclerView.adapter = adapter
    }

    fun itemsList(): ArrayList<Halqa> {
        val urls = resources.getStringArray(R.array.jangchi).toList()

        urls.forEachIndexed { index, item ->
            items.add(Halqa(bob = "${index + 1}-bob", url = item, bookName = "Halqa"))
        }
        return items
    }

    fun downloadFile(halqa: Halqa) {
        var folderName = "HalqaKitob/${halqa.bookName}"
        val request = DownloadManager.Request(Uri.parse(halqa.url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("Halqa")
            .setDescription("Halqa Audio Kitob")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalFilesDir(context, folderName,"${halqa.bookName}${halqa.bob}.mp3")
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
        refreshAdapter(items)
    }
}