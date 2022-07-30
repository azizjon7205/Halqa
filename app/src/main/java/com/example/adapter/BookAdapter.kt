package com.example.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.halqa.databinding.ItemBookChapterBinding

class BookAdapter(private val chapterTitles: List<String>, private val chapterText: List<String>) :
    RecyclerView.Adapter<BookAdapter.VH>() {

    inner class VH(val view: ItemBookChapterBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemBookChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.view.apply {
            tvChapterTitle.text = chapterTitles[position]
            tvChapterText.text = chapterText[position]
        }
    }

    override fun getItemCount(): Int = chapterTitles.size
}