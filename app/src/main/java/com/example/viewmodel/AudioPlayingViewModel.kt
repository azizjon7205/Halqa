package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.BookDao
import com.example.model.BookData
import com.example.utils.UiStateObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AudioPlayingViewModel(private val bookDao: BookDao) : ViewModel() {

    private val _singleAudio =
        MutableStateFlow<UiStateObject<BookData>>(UiStateObject.EMPTY)
    val singleAudio = _singleAudio

    private val _updated =
        MutableStateFlow<UiStateObject<Int>>(UiStateObject.EMPTY)
    val updated = _updated

    private val _updatedDownloadToTrue =
        MutableStateFlow<UiStateObject<Int>>(UiStateObject.EMPTY)
    val updatedDownloadToTrue = _updatedDownloadToTrue

    fun getAudio(id: Int) = viewModelScope.launch {
        _singleAudio.value = UiStateObject.LOADING
        try {
            val response = bookDao.getAudio(id)
            _singleAudio.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _singleAudio.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun updateDownloadStatus(id: Int, downloadID: Long) = viewModelScope.launch {
        _updated.value = UiStateObject.LOADING
        try {
            val response = bookDao.updateBookDownloadID(id, downloadID)
            _updated.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _updated.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun updateDownloadStatus(isDownloaded: Boolean, downloadID: Long) = viewModelScope.launch {
        _updatedDownloadToTrue.value = UiStateObject.LOADING
        try {
            val response = bookDao.updateDownload(isDownloaded, downloadID)
            _updatedDownloadToTrue.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _updatedDownloadToTrue.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}