/*Assignment Self Grading Live Tracker

[X] Search button added to main activity to launch Search 5%
[X] Android’s built-in Search Dialog is successfully launched when use clicks search button 15%
[X] BookDetailsFragment is updated to show book cover image 5%
[X] User is able to perform a search to the Web Service and receive all or a subset of books 25%
[X] App always shows the BookListFragment after a search is performed 15%
[X] Updates BookListFragment’s RecyclerView with new books after search is complete 20%
[X] Once retrieved, a list of books is retained if the activity is restarted (device rotated from
    portrait to landscape or vice-versa), until the user performs another search – books returned
    from new search will always replace old books 15% */

package com.example.assignment7audiobookplayer

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

lateinit var bookListVM: BookListViewModel

class MainActivity : AppCompatActivity(), BookListFragment.SelectionFragmentInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.button_Search)

        bookListVM = ViewModelProvider(this)[BookListViewModel::class.java]
        val bookViewModel: BookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        searchButton.setOnClickListener {
            onSearchRequested()
            if (supportFragmentManager.backStackEntryCount > 0)
                supportFragmentManager.popBackStack()
        }


        val fragment = supportFragmentManager.findFragmentById(R.id.container1)
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, BookListFragment())
                .commit()

        if (bookViewModel.getSelectedBook().value != null && findViewById<View>(R.id.container2) == null) {
            bookSelected()
        }
    }

    override fun bookSelected() {
        if (findViewById<View>(R.id.container2) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private suspend fun searchBooks(search: String) {

        var jsonArray: JSONArray
        var jsonObject: JSONObject
        var tempTitle: String
        var tempAuthor: String
        var tempId: Int
        var tempImg: String
        var tempBook: Book
        val tempBookList = BookList()

        withContext(Dispatchers.IO) {
            jsonArray = JSONArray(
                URL("https://kamorris.com/lab/cis3515/search.php?term=$search")
                    .openStream()
                    .bufferedReader()
                    .readLine()
            )
        }

        Log.d("TEST", jsonArray.toString())

        for (i in 0 until jsonArray.length()) {
            jsonObject = jsonArray.getJSONObject(i)
            tempTitle = jsonObject.getString("title")
            tempAuthor = jsonObject.getString("author")
            tempId = jsonObject.getInt("id")
            tempImg = jsonObject.getString("cover_url")
            tempBook = Book(tempTitle, tempAuthor, tempId, tempImg)
            tempBookList.add(tempBook)
        }

        //if(jsonArray.length() != 0){
        bookListVM.setBookList(tempBookList)
        bookListVM.increment()
        //}
    }

    override fun onNewIntent(intent: Intent?) {

        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent!!.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                CoroutineScope(Dispatchers.Main).launch {
                    searchBooks(query)
                }
            }
        }
    }
}