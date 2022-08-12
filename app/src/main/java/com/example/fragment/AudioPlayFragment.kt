package com.example.fragment

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.database.AppDatabase
import com.example.halqa.R
import com.example.halqa.databinding.FragmentAudioPlayBinding
import com.example.model.BookData
import com.example.receiver.AudioDownloadReceiver
import com.example.utils.Constants
import com.example.utils.Constants.AUDIO
import com.example.utils.Constants.BOOK_EXTRA
import com.example.utils.UiStateObject
import com.example.viewmodel.AudioPlayingViewModel
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayFragment : Fragment(R.layout.fragment_audio_play) {

    private var isPlaying: Boolean = false
    private val binding by viewBinding(FragmentAudioPlayBinding::bind)
    private var audioID: Int = -1
    private lateinit var audioPlayingViewModel: AudioPlayingViewModel
    private var appDatabase: AppDatabase? = null
    private lateinit var mediaPlayer: MediaPlayer
    var currentPosition: Int = 0
    private lateinit var audioDownloadReceiver: AudioDownloadReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = AppDatabase.getInstance(requireContext())
        audioPlayingViewModel = AudioPlayingViewModel(appDatabase!!.bookDao())

        arguments?.let {
            audioID = it.getInt(AUDIO)
        }
        sendRequestToGetAudio()

        mediaPlayer = MediaPlayer()
    }

    private fun sendRequestToGetAudio() {
        audioPlayingViewModel.getAudio(audioID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObserver()
        initViews()
    }

    private fun initViews() {

        registerDownloadBroadcast()

        binding.apply {

            tvFullDuration.isSelected = true

            ivPlayPause.setOnClickListener {
                if (isPlaying) {
                    pauseMediaPlayer()
                    ivPlayPause.setImageResource(R.drawable.ic_play_notif)
                } else {
                    playMediaPlayer()
                    ivPlayPause.setImageResource(R.drawable.ic_pause_notif)
                }
            }

            ivPrevious.setOnClickListener {

            }

            ivNext.setOnClickListener {

            }
        }
    }

    private fun registerDownloadBroadcast() {
        audioDownloadReceiver = AudioDownloadReceiver()
        requireActivity().registerReceiver(
            audioDownloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayingViewModel.singleAudio.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            setData(it.data)
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    //data setting to UI
    private fun setData(bookData: BookData) {
        if (bookData.isDownload) {
            binding.apply {
                playSource(getFilePath(getUri(bookData)))
                tvAudioTitle.text = "${bookData.bookName} ${bookData.bob}"
                tvFullDuration.text = getDuration(mediaPlayer.duration / 1000)
                setSeekBarCorrespondingly()
            }
        } else {
            downloadFile(bookData, false)
        }
    }

    private fun getUri(bookData: BookData): String =
        "${bookData.bookName}${BOOK_EXTRA}/${bookData.bookName}/${bookData.bookName}${bookData.bob}.mp3"

    private fun setSeekBarCorrespondingly() {
        binding.apply {
            seekBar.max = mediaPlayer.duration

            //seekBar and mediaPlayer
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        seekBar.progress = mediaPlayer.currentPosition
                        tvPassingDuration.text =
                            getDuration(mediaPlayer.currentPosition / 1000).toString()
                        handler.postDelayed(this, 1000)
                    } catch (e: Exception) {
                        seekBar.progress = 0
                    }
                }
            }, 0)


            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    if (p2) mediaPlayer.seekTo(p1)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}

                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
    }

    private fun playMediaPlayer() {
        mediaPlayer.seekTo(currentPosition)
        mediaPlayer.start()
        isPlaying = mediaPlayer.isPlaying
    }

    private fun pauseMediaPlayer() {
        mediaPlayer.pause()
        currentPosition = mediaPlayer.currentPosition
        isPlaying = mediaPlayer.isPlaying
    }

    private fun getFilePath(audioPath: String): String =
        "${requireContext().getExternalFilesDir(null)}/$audioPath"

    private fun getDuration(durationInSecond: Int): CharSequence = String.format(
        "%02d:%02d",
        (durationInSecond / 60) % 60,
        durationInSecond % 60
    )

    private fun playSource(filePath: String) =
        try {
            mediaPlayer.setDataSource(requireContext(), File(filePath).toUri())
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = mediaPlayer.isPlaying
            Log.d("TAG", "playSource: $filePath")
        } catch (e: Exception) {

        }

    private fun resetPlayer() =
        try {
            mediaPlayer.stop()
            mediaPlayer.reset()
            currentPosition = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }

    private fun downloadFile(bookData: BookData, isFromNotification: Boolean) {
        val folderName = "${bookData.bookName}${Constants.BOOK_EXTRA}/${bookData.bookName}"
        val request = DownloadManager.Request(Uri.parse(bookData.url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(bookData.bookName)
            .setDescription("${bookData.bookName} audio kitob ${bookData.bob}")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(false)
            .setDestinationInExternalFilesDir(
                context,
                folderName,
                "${bookData.bookName}${bookData.bob}.mp3"
            )
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadID = downloadManager.enqueue(request)
        audioPlayingViewModel.updateDownloadStatus(bookData.id!!, downloadID)
        audioDownloadReceiver.onDownloadCompleted = { ID ->
            audioPlayingViewModel.updateDownloadStatus(true, ID!!)
            setUpDownloadToTruUpdateObserver(bookData)
        }
    }

    private fun setUpDownloadToTruUpdateObserver(bookData: BookData) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayingViewModel.updatedDownloadToTrue.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            Log.d("TAG", "setUpDownloadToTruUpdateObserver: load")
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            Log.d("TAG", "setUpDownloadToTruUpdateObserver: success")
                            playSource(getFilePath(getUri(bookData)))
                        }
                        is UiStateObject.ERROR -> {
                            Log.d("TAG", "setUpDownloadToTruUpdateObserver: error ${it.message}")
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}