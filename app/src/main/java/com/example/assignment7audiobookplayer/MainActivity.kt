package com.example.assignment7audiobookplayer

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), BookListFragment.SelectionFragmentInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bookTitleArrayFromStrings: Array<String?> =
            resources.getStringArray(R.array.book_titles)
        val bookAuthorArrayFromStrings: Array<String?> =
            resources.getStringArray(R.array.book_authors)

        val bookList = BookList()
        val titleArray: Array<String> = resources.getStringArray(R.array.book_titles)
        val authorArray: Array<String> = resources.getStringArray(R.array.book_authors)

        titleArray.size

        for (i in 0..(titleArray.size-1)) {
            bookList.add(Book(titleArray[i], authorArray[i]))
            Log.d("Title:", titleArray[i])
            Log.d("Author:", titleArray[i])

        }

//        for (i in 0..10) {
//            bookList.add(Book(titleArray[i], authorArray[i]))
//            Log.d("Title:", titleArray[i])
//            Log.d("Author:", titleArray[i])
//
//        }

        val fragment = supportFragmentManager.findFragmentById(R.id.container1)
        if (fragment!= null)
            supportFragmentManager.beginTransaction().remove(fragment).commit()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container1, BookListFragment.newInstance(bookList))
            .commit()


    }


//    fun colorSelected() {
//        if (findViewById<View>(R.id.container2) == null)
//            supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.container1, BookDetailsFragment())
//                .addToBackStack(null)
//                .commit()
//    }

//    val title = findViewById<TextView>(R.id.textView_title2)
//    val author = findViewById<TextView>(R.id.textView_author2)


    override fun bookSelected() {
        if (findViewById<View>(R.id.container2) == null)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()

    }
}