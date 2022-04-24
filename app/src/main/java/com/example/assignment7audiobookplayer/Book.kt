package com.example.assignment7audiobookplayer

/*Super-simple book class to satisfy title and author*/

data class Book(val title: String,
                val author: String,
                val id: Int,
                val coverURL: String,
                val duration: Int,
                val downloaded: Boolean)