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
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.SimpleItemAnimator
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.adapter.AudioBookAdapter
import com.example.database.AppDatabase
import com.example.halqa.R
import com.example.halqa.databinding.FragmentHalqaAudioBinding
import com.example.helper.OnItemClickListener
import com.example.helper.Playable
import com.example.model.Halqa
import com.example.notification.CreateNotification
import com.example.receiver.AudioDownloadReceiver
import com.example.service.MusicService
import com.example.utils.Constants
import com.example.utils.Constants.ACTION_NAME
import com.example.utils.Constants.HALQA
import java.io.File


class HalqaAudioFragment : Fragment(R.layout.fragment_halqa_audio), Playable {

    private val binding by viewBinding(FragmentHalqaAudioBinding::bind)
    private var appDatabase: AppDatabase? = null

    private lateinit var book: String
    var filePath = ""
    var currentPosition: Int = 0
    private var lastPlayingAudioPosition: Int = -1
    private var currentPlayingAudioPosition: Int = -1
    var position = 0
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var adapter: AudioBookAdapter
    private lateinit var audioDownloadReceiver: AudioDownloadReceiver
    private lateinit var notificationManager: NotificationManager
    private lateinit var audios: List<Halqa>
    private var isPlaying = false
    private lateinit var lastPlayingHalqaChapter: Halqa

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            requireActivity().registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
            requireActivity().stopService(Intent(requireContext(), MusicService::class.java))
        }

        appDatabase?.halqaDao()?.getPosts(HALQA)?.observe(viewLifecycleOwner) {
            audios = it
            initViews()
        }
    }

    override fun onDestroyView() {
        resetPlayer()
        super.onDestroyView()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.extras?.getString(ACTION_NAME)) {

                CreateNotification.ACTION_NEXT -> onTrackNext(audios[position + 1])

                CreateNotification.ACTION_PLAY -> {
                    isPlaying = !isPlaying
                    if (isPlaying) {
                        onTrackPlay(lastPlayingHalqaChapter)
                    } else {
                        onTrackPause(lastPlayingHalqaChapter)
                    }
                }

                CreateNotification.ACTION_PREVIOUS -> onTrackPrevious(audios[position - 1])
            }
        }
    }

    override fun onTrackPrevious(halqa: Halqa) {
        position--

        if (checkPositionValidity(position)) {
            manageNotificationWithAdapter(position)
            CreateNotification.createNotification(
                requireActivity(),
                halqa,
                R.drawable.ic_play_notif,
                position,
                audios.size - 1
            )
            resetPlayer()
            playSource(getAudioPath(audios[position]))
            lastPlayingHalqaChapter = audios[position]
        } else {
            position = 0
        }
    }

    override fun onTrackPlay(halqa: Halqa) {
        Log.d("TAG", "onTrackPlay: $halqa")
        CreateNotification.createNotification(
            requireActivity(),
            halqa,
            R.drawable.ic_pause_notif,
            position,
            audios.size - 1
        )
        playMediaPlayer()
        isPlaying = mediaPlayer.isPlaying
    }

    override fun onTrackPause(halqa: Halqa) {
        Log.d("TAG", "onTrackPause: $halqa")
        CreateNotification.createNotification(
            requireActivity(),
            halqa,
            R.drawable.ic_play_notif,
            position,
            audios.size - 1
        )
        pauseMediaPlayer()
        isPlaying = mediaPlayer.isPlaying
    }

    override fun onTrackNext(halqa: Halqa) {
        position++
        if (checkPositionValidity(position)) {
            manageNotificationWithAdapter(position)
            CreateNotification.createNotification(
                requireActivity(),
                halqa,
                R.drawable.ic_pause_notif,
                position,
                audios.size - 1
            )
            resetPlayer()
            playSource(getAudioPath(audios[position]))
            lastPlayingHalqaChapter = audios[position]
        } else {
            position = audios.size
        }
    }

    private fun getAudioPath(audio: Halqa): String =
        "${requireContext().getExternalFilesDir(null)}/${Constants.HALQAKITOB}/${audio.bookName}/${audio.bookName}${audio.bob}.mp3"

    private fun getFilePath(audioPath: String): String =
        "${requireContext().getExternalFilesDir(null)}/$audioPath"

    private fun manageNotificationWithAdapter(position: Int) {
        lastPlayingAudioPosition = currentPlayingAudioPosition
        currentPlayingAudioPosition = position
        adapter.changeCurrentPlayingAudio(currentPlayingAudioPosition)
        adapter.changeLastPlayingAudio(lastPlayingAudioPosition)
        adapter.updateAudioPlayStatus(currentPlayingAudioPosition)
        adapter.updateAudioPlayStatus(lastPlayingAudioPosition)
    }

    private fun initViews() {

        //remove an animation when item is changed with notifyItemChange()
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        mediaPlayer = MediaPlayer()
        refreshAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationChannel = NotificationChannel(
            CreateNotification.CHANNEL_ID,
            "men",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun refreshAdapter() {
        adapter = AudioBookAdapter(object : OnItemClickListener {
            override fun onItemDownload(halqa: Halqa, position: Int) {
                downloadFile(halqa, position)
            }

            override fun onItemPlay(
                audioPath: String,
                seekBar: SeekBar,
                llSeek: LinearLayout,
                tvFullDuration: TextView,
                tvPassedDuration: TextView,
                lastAudioPlaying: Int,
                position: Int,
                halqa: Halqa
            ) {
                this@HalqaAudioFragment.position = position
                filePath = getFilePath(audioPath)
                lastPlayingAudioPosition = lastAudioPlaying
                currentPosition = position

                if (position == lastAudioPlaying) {
                    if (isPlaying) {
                        pauseMediaPlayer()
                        onTrackPause(halqa)
                    } else {
                        playMediaPlayer()
                        onTrackPlay(halqa)
                    }
                    adapter.updateAudioPlayStatus(position)
                } else {
                    lastPlayingHalqaChapter = halqa
                    adapter.updateAudioPlayStatus(lastAudioPlaying)
                    adapter.updateAudioPlayStatus(position)
                    resetPlayer()
                    playSource(filePath)
                    onTrackPlay(halqa)
                }

                seekBar.max = mediaPlayer.duration

                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        if (p2) mediaPlayer.seekTo(p1)
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {}

                    override fun onStopTrackingTouch(p0: SeekBar?) {}
                })

                //seekBar and mediaPlayer
                val handler = Handler()
                handler.postDelayed(object : Runnable {
                    override fun run() {
                        try {
                            seekBar.progress = mediaPlayer.currentPosition
                            tvPassedDuration.text =
                                getDuration(mediaPlayer.currentPosition / 1000).toString()
                            handler.postDelayed(this, 1000)
                        } catch (e: Exception) {
                            seekBar.progress = 0
                        }
                    }
                }, 0)

                tvFullDuration.text = getDuration(mediaPlayer.duration / 1000)
            }
        })

        adapter.submitList(audios)
        binding.recyclerView.adapter = adapter
    }

    private fun checkPositionValidity(position: Int): Boolean =
        position >= 0 && position < audios.size

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll()
        }

        requireActivity().unregisterReceiver(broadcastReceiver)
    }

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

    fun downloadFile(halqa: Halqa, position: Int) {
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

            adapter.updateAudioDownloadStatus(position)
        }
    }

    private fun playMediaPlayer() {
        mediaPlayer.seekTo(currentPosition)
        mediaPlayer.start()
    }

    private fun pauseMediaPlayer() {
        mediaPlayer.pause()
        currentPosition = mediaPlayer.currentPosition
    }
}