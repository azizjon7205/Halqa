package com.example.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.BookDao
import com.example.model.BookData
import com.example.utils.UiStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AllAudioViewModel(private val bookDao: BookDao) : ViewModel() {

    private val _allBookAudios =
        MutableStateFlow<UiStateList<BookData>>(UiStateList.EMPTY)
    val allBookAudios = _allBookAudios

    fun getBookAudios(bookName: String) = viewModelScope.launch {
        _allBookAudios.value = UiStateList.LOADING
        try {
            val response = bookDao.getBookAudios(bookName)
            _allBookAudios.value = UiStateList.SUCCESS(response)
        } catch (e: Exception) {
            _allBookAudios.value =
                UiStateList.ERROR(e.localizedMessage ?: "No connection", false)
        }
    }
}