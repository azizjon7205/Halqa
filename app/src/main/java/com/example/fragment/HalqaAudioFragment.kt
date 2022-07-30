package com.example.fragment

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.AudioBookAdapter
import com.example.database.AppDatabase
import com.example.halqa.R
import com.example.halqa.databinding.FragmentHalqaAudioBinding
import com.example.helper.OnItemClickListner
import com.example.model.Halqa
import com.example.receiver.AudioDownloadReceiver
import com.example.utils.Constants.HALQA


class HalqaAudioFragment : Fragment(R.layout.fragment_halqa_audio) {

    private val binding by viewBinding(FragmentHalqaAudioBinding::bind)
    private var appDatabase: AppDatabase? = null
    private lateinit var book: String
    var filePath = ""
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var adapter: AudioBookAdapter
    private lateinit var audioDownloadReceiver: AudioDownloadReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            book = it.getString("audio")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appDatabase = AppDatabase.getInstance(requireContext())
        audioDownloadReceiver = AudioDownloadReceiver()
        requireActivity().registerReceiver(
            audioDownloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        initViews()
    }

    private fun initViews() {
        mediaPlayer = MediaPlayer()
        refreshAdapter()
    }

    private fun refreshAdapter() {
        val items = appDatabase?.halqaDao()?.getPosts(HALQA) as ArrayList<Halqa>
        adapter = AudioBookAdapter(object : OnItemClickListner {
            override fun onItemDownload(halqa: Halqa) {
                downloadFile(halqa)
            }

            override fun onItemPlay(
                fileName: String,
                audioName: String,
                seekBar: SeekBar,
                tvSecond: TextView
            ) {

                if (mediaPlayer.isPlaying) {
                    resetPlayer()
                }
                filePath =
                    "${requireContext().getExternalFilesDir(null)}/HalqaKitob/${fileName}/$audioName"

                playSource()

                tvSecond.text = getDuration(mediaPlayer.duration / 1000)
                seekBar.max = mediaPlayer.duration / 1000
            }
        })

        adapter.submitList(items)
        binding.recyclerView.adapter = adapter
    }

    private fun getDuration(durationInSecond: Int): CharSequence? {
        return String.format(
            "%02d:%02d",
            (durationInSecond / 60) % 60,
            durationInSecond % 60
        );
    }

    private fun playSource() {
        mediaPlayer.setDataSource(requireContext(), Uri.parse(filePath))
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun resetPlayer() {
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        appDatabase?.halqaDao()?.updatePost(true, halqa.id!!)

        audioDownloadReceiver.onDownloadCompleted = {

            appDatabase?.halqaDao()?.updatePost(true, halqa.id!!)
            val items = appDatabase?.halqaDao()?.getPosts(HALQA) as ArrayList<Halqa>

            adapter.submitList(items)
        }
    }
}