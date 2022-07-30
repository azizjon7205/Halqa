package com.example.helper

import android.widget.SeekBar
import android.widget.TextView
import com.example.model.Halqa

interface OnItemClickListner {
    fun onItemDownload(halqa: Halqa)
    fun onItemPlay(fileName: String, audioName: String, seekBar: SeekBar, tvSecond: TextView)
}