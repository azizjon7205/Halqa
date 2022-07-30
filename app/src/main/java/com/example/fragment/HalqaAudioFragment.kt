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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

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

    fun downloadAudio(url: String) {
        Log.d("TAG", "downloadAudio: $url")
        val manager = activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
        val uri: Uri =
            Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        val reference: Long = manager!!.enqueue(request)
    }
}