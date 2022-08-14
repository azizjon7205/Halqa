package com.example.fragment

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.database.AppDatabase
import com.example.halqa.R
import com.example.halqa.databinding.FragmentAudioPlayBinding
import com.example.helper.Playable
import com.example.model.BookData
import com.example.notification.CreateNotification
import com.example.receiver.AudioDownloadReceiver
import com.example.utils.Constants
import com.example.utils.Constants.AUDIO
import com.example.utils.Constants.BOOK_EXTRA
import com.example.utils.Constants.HALQA
import com.example.utils.Constants.HALQA_AUIDIO_LIST_SIZE
import com.example.utils.Constants.JANGCHI_AUIDIO_LIST_SIZE
import com.example.utils.UiStateObject
import com.example.utils.hide
import com.example.utils.show
import com.example.viewmodel.AudioPlayingViewModel
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayFragment : Fragment(R.layout.fragment_audio_play), Playable {

    private var lastDownloadID: Long = 0L
    private var downloadedAudioID: Long = 0L
    private lateinit var bookData: BookData
    private var audioListSize: Int = HALQA_AUIDIO_LIST_SIZE
    private var isPlaying: Boolean = false
    private val binding by viewBinding(FragmentAudioPlayBinding::bind)
    private var audioID: Int = -1
    private lateinit var audioPlayingViewModel: AudioPlayingViewModel
    private var appDatabase: AppDatabase? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var audioDownloadReceiver: AudioDownloadReceiver
    private lateinit var notificationManager: NotificationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appDatabase = AppDatabase.getInstance(requireContext())
        audioPlayingViewModel = AudioPlayingViewModel(appDatabase!!.bookDao())

        arguments?.let {
            audioID = it.getInt(AUDIO)
            if (audioID > HALQA_AUIDIO_LIST_SIZE)
                audioListSize = JANGCHI_AUIDIO_LIST_SIZE
        }

        mediaPlayer = MediaPlayer()
        sendRequestToGetAudio()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpObserver()
        setUpNextObserver()
        setUpPreviousObserver()
        setUpDownloadIdObserver()
        initViews()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras?.getString(Constants.ACTION_NAME)) {

                CreateNotification.ACTION_NEXT -> onTrackNext()

                CreateNotification.ACTION_PLAY -> {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        onTrackPlay(bookData)
                    } else {
                        onTrackPause(bookData)
                    }
                }

                CreateNotification.ACTION_PREVIOUS -> onTrackPrevious()
            }
        }
    }

    override fun onTrackPrevious() {
        audioID--
        managePreviousLastVisibility(isFirstAudio(), binding.ivPrevious)
        managePreviousLastVisibility(isLastAudio(), binding.ivNext)
        if (checkAudioIdValidity(audioID)) {
            resetPlayer()
            sendRequestToGetPreviousAudio()
        } else {
            audioID = if (audioListSize == HALQA_AUIDIO_LIST_SIZE) 1 else 33
        }
    }

    override fun onTrackPause(bookData: BookData) {
        binding.ivPlayPause.setImageResource(R.drawable.ic_play_notif)
        createNotification(bookData, R.drawable.ic_play_notif)
        pauseMediaPlayer()
        isPlaying = mediaPlayer.isPlaying
    }

    override fun onTrackPlay(bookData: BookData) {
        binding.ivPlayPause.setImageResource(R.drawable.ic_pause_notif)
        createNotification(bookData, R.drawable.ic_pause_notif)
        playMediaPlayer()
        isPlaying = mediaPlayer.isPlaying
    }

    override fun onTrackNext() {
        audioID++
        managePreviousLastVisibility(isLastAudio(), binding.ivNext)
        managePreviousLastVisibility(isFirstAudio(), binding.ivPrevious)
        if (checkAudioIdValidity(audioID)) {
            resetPlayer()
            sendRequestToGetNextAudio()
        } else {
            audioID = if (audioListSize == HALQA_AUIDIO_LIST_SIZE)
                32
            else
                46
        }
    }

    private fun checkAudioIdValidity(audioID: Int): Boolean {
        return if (bookData.bookName == HALQA) audioID in 1..32
        else audioID in 33..46
    }

    private fun isFirstAudio(): Boolean =
        if (bookData.bookName == HALQA) audioID == 1 else audioID == 33

    private fun isLastAudio(): Boolean =
        if (bookData.bookName == HALQA) audioID == 32 else audioID == 46

    private fun sendRequestToGetAudio() {
        audioPlayingViewModel.getAudio(audioID)
    }

    private fun sendRequestToGetPreviousAudio() {
        audioPlayingViewModel.getPreviousAudio(audioID)
    }

    private fun sendRequestToGetNextAudio() {
        audioPlayingViewModel.getNextAudio(audioID)
    }

    private fun createNotification(bookData: BookData, drawable: Int) {
        CreateNotification.createNotification(
            requireActivity(),
            bookData,
            drawable,
            audioID,
            audioListSize
        )
    }

    private fun initViews() {

        registerDownloadBroadcast()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            requireActivity().registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
        }

        binding.apply {

            tvFullDuration.isSelected = true

            ivPlayPause.setOnClickListener {
                if (isPlaying) {
                    pauseMediaPlayer()
                    ivPlayPause.setImageResource(R.drawable.ic_play_notif)
                    onTrackPause(bookData)
                } else {
                    playMediaPlayer()
                    ivPlayPause.setImageResource(R.drawable.ic_pause_notif)
                    onTrackPlay(bookData)
                }
            }

            ivPrevious.setOnClickListener {
                audioID--
                if (checkAudioIdValidity(audioID)) {
                    resetPlayer()
                    sendRequestToGetPreviousAudio()
                }
                managePreviousLastVisibility(isFirstAudio(), ivPrevious)
                managePreviousLastVisibility(isLastAudio(), ivNext)
            }

            ivNext.setOnClickListener {
                audioID++
                if (checkAudioIdValidity(audioID)) {
                    resetPlayer()
                    sendRequestToGetNextAudio()
                }
                managePreviousLastVisibility(isLastAudio(), ivNext)
                managePreviousLastVisibility(isFirstAudio(), ivPrevious)
            }
        }
    }

    private fun managePreviousLastVisibility(isTarget: Boolean, view: View) {
        if (isTarget) {
            view.hide()
        } else {
            view.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            CreateNotification.CHANNEL_ID,
            "men",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager =
            requireActivity().getSystemService(NotificationManager::class.java)
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel)
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
                            bookData = it.data
                            setData(it.data)
                            //setUpDownloadToTruUpdateObserver(bookData)
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
        Log.d("TAG", "setUpObserver setData: $bookData")
        if (bookData.isDownload) {
            resetPlayer()
            if (!mediaPlayer.isPlaying)
                playSource(getFilePath(getUri(bookData)))
            hideProgress()
            binding.apply {
                tvAudioTitle.text = "${bookData.bookName} ${bookData.bob}"
                managePreviousLastVisibility(isFirstAudio(), binding.ivPrevious)
                managePreviousLastVisibility(isLastAudio(), binding.ivNext)
                tvFullDuration.text = getDuration(mediaPlayer.duration / 1000)
                setSeekBarCorrespondingly()
                onTrackPlay(bookData)
            }
        } else {
            showProgress(bookData)
            downloadFile(bookData, false)
        }
    }

    private fun showProgress(bookData: BookData) {
        binding.downloadProgress.visibility = View.VISIBLE
        binding.tvAudioTitle.text = "Yuklanmoqda... (${bookData.bookName} ${bookData.bob})"
    }

    private fun hideProgress() {
        binding.downloadProgress.visibility = View.GONE
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
        mediaPlayer.start()
        isPlaying = mediaPlayer.isPlaying
    }

    private fun pauseMediaPlayer() {
        mediaPlayer.pause()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }

    private fun downloadFile(bookData: BookData, isFromNotification: Boolean) {
        resetSeekBar()
        Log.d("TAG", "setUpObserver downloadFile: $bookData")
        val folderName = "${bookData.bookName}${BOOK_EXTRA}/${bookData.bookName}"
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
        lastDownloadID = downloadID
        Log.d("TAG", "setUpObserver downloadID: $downloadID")
        audioPlayingViewModel.checkIsDownloadIDChange(bookData.id)

        audioDownloadReceiver.onDownloadCompleted = { ID ->
            downloadedAudioID = ID!!
            Log.d("TAG", "setUpObserver ID: $ID")
            audioPlayingViewModel.updateDownloadStatus(true, ID)
            audioPlayingViewModel.checkIsDownloadIDChange(bookData.id)
        }
    }

    private fun resetSeekBar() {
        binding.apply {
            seekBar.max = 1
            seekBar.progress = 0
            tvPassingDuration.text = getString(R.string.str_start)
            tvFullDuration.text = getString(R.string.str_start)
        }
    }

    private fun setUpDownloadIdObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayingViewModel.downloadId.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            Log.d(
                                "TAG",
                                "setUpObserver downloadID:${downloadedAudioID} $lastDownloadID  ${it.data}"
                            )
                            if (it.data == -1L) {
                                audioPlayingViewModel.updateDownloadStatus(
                                    bookData.id!!,
                                    lastDownloadID
                                )
                            }
                            if (it.data == lastDownloadID && lastDownloadID == downloadedAudioID) {
                                bookData.isDownload = true
                                setData(bookData)
                            }
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setUpPreviousObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayingViewModel.previousAudio.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            Log.d("TAG", "setUpObserver Previous: ${it.data}")
                            bookData = it.data
                            setData(it.data)
                            //createNotification(it.data, R.drawable.ic_pause_notif)
                        }
                        is UiStateObject.ERROR -> {
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun setUpNextObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                audioPlayingViewModel.nextAudio.collect {
                    when (it) {
                        UiStateObject.LOADING -> {
                            //show progress
                        }

                        is UiStateObject.SUCCESS -> {
                            Log.d("TAG", "setUpObserver Next: ${it.data}")
                            bookData = it.data
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

}