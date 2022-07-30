package com.example.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.halqa.R
import com.example.halqa.databinding.ItemAudioBookBinding
import com.example.helper.OnItemClickListner
import com.example.model.Halqa

class AudioBookAdapter(private var onItemClickListner: OnItemClickListner) :
    ListAdapter<Halqa, RecyclerView.ViewHolder> (DiffUtil()) {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder.ItemBookChapter {
        val view = ItemAudioBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder.ItemBookChapter(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder){
            is ViewHolder.ItemBookChapter ->{
                holder.view.apply {

                    tvBob.text = item.bob

                    if (item.isDownload){
                        ivPlay.setImageResource(R.drawable.ic_play)
                    }else{
                        ivPlay.setImageResource(R.drawable.ic_download)
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

    class DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<Halqa>() {
        override fun areItemsTheSame(oldItem: Halqa, newItem: Halqa): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Halqa, newItem: Halqa): Boolean {
            return oldItem == newItem
        }
    }

    sealed class ViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        class ItemBookChapter(val view: ItemAudioBookBinding) : ViewHolder(view)
    }
}