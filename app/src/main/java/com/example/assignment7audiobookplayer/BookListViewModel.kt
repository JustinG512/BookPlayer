package com.example.assignment7audiobookplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData

class BookListViewModel : ViewModel() {

    private var x : Int = 0
    private val viewModelBookList : BookList = BookList()

    private val change : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun increment(){
        change.value = x + 1
        x++
    }

    fun getIncrement() : LiveData<Int> {
        return change
    }

    fun setBookList(bookList : BookList) {
        viewModelBookList.clear()

        for(i in 0 until bookList.size()){
            viewModelBookList.add(bookList[i])
        }
    }

    fun getBookList() : BookList {
        return viewModelBookList
    }

}