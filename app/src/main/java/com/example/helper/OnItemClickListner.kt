package com.example.helper

import com.example.model.Halqa

interface OnItemClickListner {
    fun onItemDownload(halqa: Halqa)
    fun onItemPlay(fileName: String, audioName: String)
}