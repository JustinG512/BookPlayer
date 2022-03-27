package com.example.assignment7audiobookplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*This class was built in class during the demo and modified to fit the assignment.*/
class BookViewModel : ViewModel() {

    private val selectedBook : MutableLiveData<Book> by lazy {
        MutableLiveData<Book>()
    }

    fun setSelectedBook(book: Book) {
        selectedBook.value = book
    }

    fun getSelectedBook() : LiveData<Book> {
        return selectedBook
    }

}