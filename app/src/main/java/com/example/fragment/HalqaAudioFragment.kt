package com.example.fragment

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.AudioBookAdapter
import com.example.halqa.R
import com.example.halqa.databinding.FragmentHalqaAudioBinding
import com.example.model.Halqa


class HalqaAudioFragment : Fragment(R.layout.fragment_halqa_audio) {
    private val binding by viewBinding(FragmentHalqaAudioBinding::bind)
    lateinit var folderName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        folderName = "HalqaKitob/kitob"

        refreshAdapter(itemsList())
    }

    fun refreshAdapter(items: ArrayList<Halqa>) {
        val adapter = AudioBookAdapter(this, items)
        binding.recyclerView.adapter = adapter
    }

    fun itemsList(): ArrayList<Halqa> {
        val items = ArrayList<Halqa>()
        val urls = resources.getStringArray(R.array.jangchi).toList()

        Log.d("@@@", "UrlList: $urls")

        urls.forEachIndexed { index, item ->
            items.add(Halqa("${index + 1}-bob", item))
        }

        Log.d("@@@", "itemsList: $items")

        return items
    }

    fun downloadFile(url: String) {

        // fileName -> fileName with extension
        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("Halqa")
            .setDescription("Halqa Audio Kitob")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalFilesDir(context, folderName, "HalqaKitob.mp3")
        val downloadManager =
            activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
    }

}