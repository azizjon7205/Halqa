package com.example.helper

import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import com.example.model.Halqa

interface OnItemClickListener {

    fun onItemDownload(halqa: Halqa, position: Int)

    fun onItemPlay(
        audioPath: String,
        seekBar: SeekBar,
        llSeek: LinearLayout,
        tvFullDuration: TextView,
        tvPassedDuration: TextView,
        lastAudioPlaying: Int,
        position: Int,
        halqa: Halqa
    )
}