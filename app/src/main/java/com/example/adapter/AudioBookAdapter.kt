package com.example.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fragment.HalqaAudioFragment
import com.example.halqa.R
import com.example.halqa.databinding.ItemAudioBookBinding
import com.example.helper.OnItemClickListner
import com.example.model.Halqa

class AudioBookAdapter(var fragment: HalqaAudioFragment, var items: ArrayList<Halqa>, private var onItemClickListner: OnItemClickListner) :
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

                if (item.isDownload){
                    ivPlay.setImageResource(R.drawable.ic_play)
                }else{

                }

                ivPlay.setOnClickListener {
                   if (item.isDownload){
                       onItemClickListner.onItemPlay(item.bookName, "${item.bookName}${item.bob}.mp3")
                   }else{
                       onItemClickListner.onItemDownload(item)
                   }
                }
            }
        }
    }
}