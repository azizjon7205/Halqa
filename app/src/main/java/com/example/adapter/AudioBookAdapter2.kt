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

class AudioBookAdapter2(private val onItemClick: ((BookData) -> Unit)) :
    RecyclerView.Adapter<AudioBookAdapter2.VH>() {

    private val audioList: ArrayList<BookData> = arrayListOf()

    inner class VH(val binding: ItemAudioBookBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
        ItemAudioBookBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.binding.apply {
            val audio = audioList[position]
            tvAudioName.text = audio.bookName + " " + audio.bob + " (Audio)"
            tvArtist.text = "Abdukarim Mirzayev"
            if (audio.bookName == JANGCHI)
                ivBookImage.setImageResource(R.drawable.jangchi)

            this.root.setOnClickListener {
                onItemClick.invoke(audio)
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
}