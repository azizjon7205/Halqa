//package com.example.fragment
//
//import android.app.DownloadManager
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.content.IntentFilter
//import android.media.AudioManager
//import android.media.MediaPlayer
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.view.View
//import android.widget.LinearLayout
//import android.widget.SeekBar
//import android.widget.TextView
//import androidx.annotation.RequiresApi
//import androidx.core.net.toUri
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.SimpleItemAnimator
//import by.kirich1409.viewbindingdelegate.viewBinding
//import com.example.adapter.AudioBookAdapter
//import com.example.database.AppDatabase
//import com.example.halqa.R
//import com.example.halqa.databinding.FragmentHalqaAudioBinding
//import com.example.helper.OnItemClickListener
//import com.example.helper.Playable
//import com.example.model.BookData
//import com.example.notification.CreateNotification
//import com.example.receiver.AudioDownloadReceiver
//import com.example.utils.Constants
//import com.example.utils.Constants.ACTION_NAME
//import com.example.utils.Constants.AUDIO
//import com.example.utils.Constants.BOOK_EXTRA
//import com.example.utils.Constants.HALQA
//import com.example.utils.Constants.JANGCHI
//import java.io.File
//
//
//class HalqaAudioFragment : Fragment(R.layout.fragment_halqa_audio), Playable {
//
//    private val binding by viewBinding(FragmentHalqaAudioBinding::bind)
//
//    private var appDatabase: AppDatabase? = null
//    private lateinit var book: String
//    var filePath = ""
//    var currentPosition: Int = 0
//    private var lastPlayingAudioPosition: Int = -1
//    private var currentPlayingAudioPosition: Int = -1
//    var position = 0
//    private lateinit var mediaPlayer: MediaPlayer
//    private lateinit var adapter: AudioBookAdapter
//    private lateinit var audioDownloadReceiver: AudioDownloadReceiver
//    private lateinit var notificationManager: NotificationManager
//    private lateinit var audios: List<BookData>
//    private var isPlaying = false
//    private lateinit var lastPlayingChapter: BookData
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        arguments?.let {
//            book = it.getString(AUDIO)!!
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        appDatabase = AppDatabase.getInstance(requireContext())
//        audioDownloadReceiver = AudioDownloadReceiver()
//
//        registerDownloadBroadcast()
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createChannel()
//            requireActivity().registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
//        }
//
//        if (book == HALQA)
//            appDatabase?.bookDao()?.getPosts(HALQA)?.observe(viewLifecycleOwner) {
//                audios = it
//                initViews()
//            }
//        else {
//            appDatabase?.bookDao()?.getPosts(JANGCHI)?.observe(viewLifecycleOwner) {
//                audios = it
//                initViews()
//            }
//        }
//    }
//
//    private fun registerDownloadBroadcast() {
//        requireActivity().registerReceiver(
//            audioDownloadReceiver,
//            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//        )
//    }
//
//    override fun onDestroyView() {
//        resetPlayer()
//        super.onDestroyView()
//    }
//
//    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.extras?.getString(ACTION_NAME)) {
//
//                CreateNotification.ACTION_NEXT -> onTrackNext()
//
//                CreateNotification.ACTION_PLAY -> {
//                    adapter.updateAudioPlayStatus(position)
//                    isPlaying = !isPlaying
//                    if (isPlaying) {
//                        onTrackPlay()
//                    } else {
//                        onTrackPause()
//                    }
//                }
//
//                CreateNotification.ACTION_PREVIOUS -> onTrackPrevious()
//            }
//        }
//    }
//
//    override fun onTrackPrevious() {
//        position--
//
//        if (checkPositionValidity(position)) {
//            val audio = audios[position]
//            CreateNotification.createNotification(
//                requireActivity(),
//                bookData,
//                R.drawable.ic_pause_notif,
//                position,
//                audios.size - 1
//            )
//            resetPlayer()
//            if (audio.isDownload) {
//                manageNotificationWithAdapter(position)
//                playSource(getAudioPath(audio))
//                lastPlayingChapter = audio
//            } else {
//                downloadFile(bookData, position, true)
//
//            }
//        } else {
//            position = 0
//        }
//    }
//
//    override fun onTrackPlay() {
//        CreateNotification.createNotification(
//            requireActivity(),
//            bookData,
//            R.drawable.ic_pause_notif,
//            position,
//            audios.size - 1
//        )
//        playMediaPlayer()
//        isPlaying = mediaPlayer.isPlaying
//    }
//
//    override fun onTrackPause() {
//        CreateNotification.createNotification(
//            requireActivity(),
//            bookData,
//            R.drawable.ic_play_notif,
//            position,
//            audios.size - 1
//        )
//        pauseMediaPlayer()
//        isPlaying = mediaPlayer.isPlaying
//    }
//
//    override fun onTrackNext() {
//        position++
//        if (checkPositionValidity(position)) {
//            control(position)
//            val audio = audios[position]
//            CreateNotification.createNotification(
//                requireActivity(),
//                bookData,
//                R.drawable.ic_pause_notif,
//                position,
//                audios.size - 1
//            )
//            resetPlayer()
//            if (audio.isDownload) {
//                manageNotificationWithAdapter(position)
//                playSource(getAudioPath(audio))
//                lastPlayingChapter = audio
//            } else {
//                downloadFile(bookData, position, true)
//            }
//        } else {
//            position = audios.size
//        }
//    }
//
//    private fun control(position: Int) {
//        binding.recyclerView.postDelayed(Runnable() {
//            fun run() {
//                binding.recyclerView.scrollToPosition(position);
//            }
//        }, 300)
//
//
//        binding.recyclerView.postDelayed(Runnable() {
//            run() {
//                binding.recyclerView.findViewHolderForAdapterPosition(position)?.itemView?.performClick();
//            }
//        }, 400)
//    }
//
//    private fun getAudioPath(audio: BookData): String =
//        "${requireContext().getExternalFilesDir(null)}/${Constants.HALQAKITOB}/${audio.bookName}/${audio.bookName}${audio.bob}.mp3"
//
//    private fun getFilePath(audioPath: String): String =
//        "${requireContext().getExternalFilesDir(null)}/$audioPath"
//
//    private fun manageNotificationWithAdapter(position: Int) {
//        lastPlayingAudioPosition = currentPlayingAudioPosition
//        currentPlayingAudioPosition = position
//        adapter.changeCurrentPlayingAudio(currentPlayingAudioPosition)
//        adapter.changeLastPlayingAudio(lastPlayingAudioPosition)
//        Log.d(
//            "TAG",
//            "manageNotificationWithAdapter: $lastPlayingAudioPosition $currentPlayingAudioPosition"
//        )
//        adapter.updateAudioPlayStatus(lastPlayingAudioPosition)
//        adapter.updateAudioPlayStatus(currentPlayingAudioPosition)
//    }
//
//    private fun initViews() {
//
//        //remove an animation when item is changed with notifyItemChange()
//        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//
//        mediaPlayer = MediaPlayer()
//        refreshAdapter()
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createChannel() {
//        val notificationChannel = NotificationChannel(
//            CreateNotification.CHANNEL_ID,
//            "men",
//            NotificationManager.IMPORTANCE_LOW
//        )
//
//        notificationManager = requireActivity().getSystemService(NotificationManager::class.java)
//        if (notificationManager != null) {
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//
//    }
//
//    private fun refreshAdapter() {
//        adapter = AudioBookAdapter(object : OnItemClickListener {
//            override fun onItemDownload(halqa: BookData, position: Int) {
//                downloadFile(halqa, position, false)
//            }
//
//            override fun onItemPlay(
//                audioPath: String,
//                seekBar: SeekBar,
//                llSeek: LinearLayout,
//                tvFullDuration: TextView,
//                tvPassedDuration: TextView,
//                lastAudioPlaying: Int,
//                position: Int,
//                bookData: BookData
//            ) {
//                Log.d("TAG", "onItemPlay: $bookData")
//                this@HalqaAudioFragment.position = position
//                filePath = getFilePath(audioPath)
//                lastPlayingAudioPosition = lastAudioPlaying
//                currentPosition = position
//                currentPlayingAudioPosition = position
//                this@HalqaAudioFragment.isPlaying = bookData.isPlaying
//                if (position == lastAudioPlaying) {
//                    if (isPlaying) {
//                        pauseMediaPlayer()
//                        onTrackPause()
//                    } else {
//                        playMediaPlayer()
//                        onTrackPlay()
//                    }
//                    adapter.updateAudioPlayStatus(position)
//                } else {
//                    lastPlayingChapter = bookData
//                    adapter.updateAudioPlayStatus(lastAudioPlaying)
//                    adapter.updateAudioPlayStatus(position)
//                    resetPlayer()
//                    playSource(filePath)
//                    onTrackPlay()
//                }
//
//                seekBar.max = mediaPlayer.duration
//
//                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                        if (p2) mediaPlayer.seekTo(p1)
//                    }
//
//                    override fun onStartTrackingTouch(p0: SeekBar?) {}
//
//                    override fun onStopTrackingTouch(p0: SeekBar?) {}
//                })
//
//                //seekBar and mediaPlayer
//                val handler = Handler()
//                handler.postDelayed(object : Runnable {
//                    override fun run() {
//                        try {
//                            seekBar.progress = mediaPlayer.currentPosition
//                            tvPassedDuration.text =
//                                getDuration(mediaPlayer.currentPosition / 1000)
//                            handler.postDelayed(this, 1000)
//                        } catch (e: Exception) {
//                            seekBar.progress = 0
//                        }
//                    }
//                }, 0)
//
//                tvFullDuration.text = getDuration(mediaPlayer.duration / 1000)
//            }
//        })
//
//        adapter.submitList(audios)
//        binding.recyclerView.adapter = adapter
//    }
//
//    private fun checkPositionValidity(position: Int): Boolean =
//        position >= 0 && position < audios.size
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationManager.cancelAll()
//        }
//
//        requireActivity().unregisterReceiver(broadcastReceiver)
//    }
//
//    private fun getDuration(durationInSecond: Int): CharSequence = String.format(
//        "%02d:%02d",
//        (durationInSecond / 60) % 60,
//        durationInSecond % 60
//    )
//
//    private fun playSource(filePath: String) =
//        try {
//            mediaPlayer.setDataSource(requireContext(), File(filePath).toUri())
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
//            mediaPlayer.prepare()
//            mediaPlayer.start()
//            isPlaying = mediaPlayer.isPlaying
//        } catch (e: Exception) {
//        }
//
//    private fun resetPlayer() =
//        try {
//            mediaPlayer.stop()
//            mediaPlayer.reset()
//            currentPosition = 0
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    private fun downloadFile(bookData: BookData, position: Int, isFromNotification: Boolean) {
//        val folderName = "${bookData.bookName}${BOOK_EXTRA}/${bookData.bookName}"
//        val request = DownloadManager.Request(Uri.parse(bookData.url))
//            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
//            .setTitle("Halqa")
//            .setDescription("${bookData.bookName} audio kitob ${bookData.bob}")
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            .setAllowedOverMetered(true)
//            .setAllowedOverRoaming(false)
//            .setDestinationInExternalFilesDir(
//                context,
//                folderName,
//                "${bookData.bookName}${bookData.bob}.mp3"
//            )
//        val downloadManager =
//            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val downloadID = downloadManager.enqueue(request)
//        appDatabase?.bookDao()?.updateBookDownloadID(bookData.id!!, downloadID)
//        adapter.changeAudioDownloadID(position, downloadID)
//        audioDownloadReceiver.onDownloadCompleted = { ID ->
//            appDatabase?.bookDao()?.updatePost(true, ID!!)
//            adapter.updateAudioDownloadStatus(ID)
//            if (isFromNotification) {
//                playSource(getAudioPath(bookData))
//                lastPlayingChapter = bookData
//                manageNotificationWithAdapter(position)
//            }
//        }
//    }
//
//    private fun playMediaPlayer() {
//        mediaPlayer.seekTo(currentPosition)
//        mediaPlayer.start()
//        isPlaying = mediaPlayer.isPlaying
//    }
//
//    private fun pauseMediaPlayer() {
//        mediaPlayer.pause()
//        currentPosition = mediaPlayer.currentPosition
//        isPlaying = mediaPlayer.isPlaying
//    }
//}