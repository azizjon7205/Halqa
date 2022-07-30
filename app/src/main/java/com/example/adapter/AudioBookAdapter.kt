package com.example.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment.HalqaAudioFragment
import com.example.halqa.databinding.ItemAudioBookBinding
import com.example.model.Halqa

class AudioBookAdapter(var fragment: HalqaAudioFragment, var items: ArrayList<Halqa>) :
    RecyclerView.Adapter<AudioBookAdapter.AudioBookViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioBookViewHolder {
        return AudioBookViewHolder(
            ItemAudioBookBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AudioBookViewHolder, position: Int) = holder.bind()

    inner class AudioBookViewHolder(val binding: ItemAudioBookBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item:Halqa = items[adapterPosition]

            binding.apply {
                tvBob.text = item.bob
                ivPlay.setOnClickListener {
                    //fragment.downloadAudio(item.url)
                    fragment.downloadFile(item.url)
                }
            }
        }
    }
}