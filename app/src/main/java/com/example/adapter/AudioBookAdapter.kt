package com.example.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.halqa.R
import com.example.halqa.databinding.ItemAudioBookBinding
import com.example.helper.OnItemClickListener
import com.example.model.Halqa
import com.example.utils.Constants
import com.example.utils.hide
import com.example.utils.show
import kotlin.math.log

class AudioBookAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AudioBookAdapter.VH>() {

    private val audioList: ArrayList<Halqa> = arrayListOf()
    private var selectedAudioPosition = -1
    private var lastAudioSelectedPosition = -1

    inner class VH(val binding: ItemAudioBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        ItemAudioBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.binding.apply {
            val audio = audioList[position]
            Log.d("TAG", "onBindViewHolder: $audio")
            tvBob.text = audio.bob

            if (audio.isDownload) {
                if (audio.isPlaying) {
                    ivPlay.setImageResource(R.drawable.ic_pause)
                    llSeek.show()
                } else {
                    ivPlay.setImageResource(R.drawable.ic_play)
                    llSeek.hide()
                }
            } else ivPlay.setImageResource(R.drawable.ic_download)

            ivPlay.setOnClickListener {
                when (audio.isDownload) {
                    true -> {
                        lastAudioSelectedPosition = selectedAudioPosition
                        selectedAudioPosition = holder.absoluteAdapterPosition

                        if (audio.isPlaying) {
                            ivPlay.setImageResource(R.drawable.ic_pause)
                            llSeek.show()
                        } else {
                            ivPlay.setImageResource(R.drawable.ic_play)
                            llSeek.hide()
                        }

                        onItemClickListener.onItemPlay(
                            "${Constants.HALQAKITOB}/${audio.bookName}/${audio.bookName}${audio.bob}.mp3",
                            seekBar,
                            llSeek,
                            tvFullDuration,
                            tvPassedDuration,
                            lastAudioSelectedPosition,
                            selectedAudioPosition,
                            audio
                        )
                    }
                    false -> {
                        onItemClickListener.onItemDownload(audio, position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = audioList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun submitList(audioList: List<Halqa>) {
        this.audioList.addAll(audioList)
    }

    fun updateAudioPlayStatus(position: Int) {
        if (position == -1)
            return
        audioList[position].isPlaying = !audioList[position].isPlaying
        notifyItemChanged(position)
    }

    fun updateAudioDownloadStatus(position: Int) {
        this.audioList[position].isDownload = true
        notifyItemChanged(position)
    }

    fun changeCurrentPlayingAudio(position: Int) {
        this.selectedAudioPosition = position
    }


    fun changeLastPlayingAudio(position: Int) {
        this.lastAudioSelectedPosition = position
    }
}