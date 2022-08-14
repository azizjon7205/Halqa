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

    private val _nextAudio =
        MutableStateFlow<UiStateObject<BookData>>(UiStateObject.EMPTY)
    val nextAudio = _nextAudio

    private val _previousAudio =
        MutableStateFlow<UiStateObject<BookData>>(UiStateObject.EMPTY)
    val previousAudio = _previousAudio

    private val _updated =
        MutableStateFlow<UiStateObject<Int>>(UiStateObject.EMPTY)
    val updated = _updated

    private val _downloadId =
        MutableStateFlow<UiStateObject<Long>>(UiStateObject.EMPTY)
    val downloadId = _downloadId

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

    fun getNextAudio(id: Int) = viewModelScope.launch {
        _nextAudio.value = UiStateObject.LOADING
        try {
            val response = bookDao.getAudio(id)
            _nextAudio.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _nextAudio.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }

    fun getPreviousAudio(id: Int) = viewModelScope.launch {
        _previousAudio.value = UiStateObject.LOADING
        try {
            val response = bookDao.getAudio(id)
            _previousAudio.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _previousAudio.value =
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

    fun checkIsDownloadIDChange(id: Int?) = viewModelScope.launch {
        _downloadId.value = UiStateObject.LOADING
        try {
            val response = bookDao.getDownloadId(id)
            _downloadId.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _downloadId.value =
                UiStateObject.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}