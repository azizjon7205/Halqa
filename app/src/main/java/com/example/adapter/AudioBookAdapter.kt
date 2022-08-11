package com.example.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.halqa.R
import com.example.halqa.databinding.ItemAudioBookBinding
import com.example.helper.OnItemClickListener
import com.example.model.BookData
import com.example.utils.Constants.BOOK_EXTRA
import com.example.utils.Constants.JANGCHI
import com.example.utils.hide
import com.example.utils.invisible
import com.example.utils.show

class AudioBookAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<AudioBookAdapter.VH>() {

    private val audioList: ArrayList<BookData> = arrayListOf()
    private var selectedAudioPosition = -1
    private var lastAudioSelectedPosition = -1

    inner class VH(val binding: ItemAudioBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        ItemAudioBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.d("TAG", "onBindViewHolder: came $position")
        holder.binding.apply {
            val audio = audioList[position]
            tvBob.text = audio.bob
            tvAudioName.text = audio.bookName + " (Audio)"
            if (audio.bookName == JANGCHI)
                ivBookImage.setImageResource(R.drawable.jangchi)

            if (audio.isDownload) {
                progressWrapper.hide()
                if (audio.isPlaying) {
                    ivPlay.setImageResource(R.drawable.ic_pause)
                    llSeek.show()
                } else {
                    ivPlay.setImageResource(R.drawable.ic_play)
                    llSeek.hide()
                }
            } else ivPlay.setImageResource(R.drawable.ic_download)

            ivPlay.setOnClickListener {
                if (audio.isDownload) {
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
                        "${audio.bookName}${BOOK_EXTRA}/${audio.bookName}/${audio.bookName}${audio.bob}.mp3",
                        seekBar,
                        llSeek,
                        tvFullDuration,
                        tvPassedDuration,
                        lastAudioSelectedPosition,
                        selectedAudioPosition,
                        audio
                    )
                } else {
                    progressWrapper.show()
                    onItemClickListener.onItemDownload(audio, position)
                }
            }
        }
    }

    override fun getItemCount(): Int = audioList.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun submitList(audioList: List<BookData>) {
        this.audioList.addAll(audioList)
    }

    fun updateAudioPlayStatus(position: Int) {
        if (position == -1)
            return
        audioList[position].isPlaying = !audioList[position].isPlaying
        notifyItemChanged(position)
    }

    fun updateAudioDownloadStatus(ID: Long?) {
        this.audioList.forEachIndexed { index, bookData ->
            if (bookData.downloadID == ID) {
                this.audioList[index].isDownload = true
                notifyItemChanged(index)
                return
            }
        }
    }

    fun changeCurrentPlayingAudio(position: Int) {
        this.selectedAudioPosition = position
    }


    fun changeLastPlayingAudio(position: Int) {
        this.lastAudioSelectedPosition = position
    }

    fun changeAudioDownloadID(position: Int, downloadID: Long) {
        this.audioList[position].downloadID = downloadID
    }
}