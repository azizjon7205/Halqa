package com.example.helper

import com.example.model.Halqa

interface Playable {

    fun onTrackPrevious(halqa: Halqa)
    fun onTrackPlay(halqa: Halqa)
    fun onTrackPause(halqa: Halqa)
    fun onTrackNext(halqa: Halqa)

}