package com.example.helper

import com.example.model.BookData

interface Playable {

    fun onTrackPrevious()
    fun onTrackPlay(bookData: BookData)
    fun onTrackPause(bookData: BookData)
    fun onTrackNext()

}