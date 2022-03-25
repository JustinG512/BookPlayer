package com.example.assignment7audiobookplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity(), SelectionFragment.SelectionFragmentInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        val bookList = BookList()
//        val titleArray: Array<String> = resources.getStringArray(R.array.book_titles)
//        val authorArray: Array<String> = resources.getStringArray(R.array.book_authors)
//
//        for (i in 0..9) {
//            bookList.add(Book(titleArray[i], authorArray[i]))
//        }

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container1, SelectionFragment.newInstance(getColors()))
            .commit()

    }

    fun getColors(): Array<String> = arrayOf(
//        "Red",
//        "Black",
//        "Green",
//        "Blue",
//        "Yellow",
        "White"
//        "Magenta",
//        "Grey",
//        "Teal",
//        "Lime",
//        "Maroon",
//        "Navy"
    )

    override fun colorSelected() {
        if (findViewById<View>(R.id.container2) == null)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
    }
}