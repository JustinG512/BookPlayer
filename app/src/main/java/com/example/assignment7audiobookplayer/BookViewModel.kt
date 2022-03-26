package com.example.assignment7audiobookplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel : ViewModel() {
    val selectedColor : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun setSelectedBook(book: Book) {
        selectedColor.value = book.toString()
    }

    fun getSelectedColor() : LiveData<String> {
        return selectedColor
    }

}