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

        // This will open the search dialog.  This code is very similar to what was
        // done in class during the demo.
        searchButton.setOnClickListener {
            onSearchRequested()
            if (supportFragmentManager.backStackEntryCount > 0)
                supportFragmentManager.popBackStack()
        }


        // Container 1
        val fragment = supportFragmentManager.findFragmentById(R.id.container1)
        if (fragment != null) {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }

        // Container 1
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, BookListFragment())
                .commit()

        // Container 2
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

        // Example:
        // {"id":"2","title":"The Island of Doctor Moreau","author":"H. G. Wells","cover_url"
        // :"https:\/\/kamorris.com\/lab\/abp\/covers\/IslandOfDrMoreau.jpeg"}
        var jsonArray: JSONArray
        var jsonObject: JSONObject
        var tempTitle: String //"title":"The Island of Doctor Moreau"
        var tempAuthor: String //"author":"H. G. Wells
        var tempId: Int //"id":"2"
        var tempCover: String // "cover_url":"https:\/\/kamorris.com\/lab\/abp\/covers\/IslandOfDrMoreau.jpeg"
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


        // This will sort through the incoming json data until it is empty.  Results will
        // be returned accordingly
        for (i in 0 until jsonArray.length()) {
            jsonObject = jsonArray.getJSONObject(i)
            tempTitle = jsonObject.getString("title")
            tempAuthor = jsonObject.getString("author")
            tempId = jsonObject.getInt("id")
            tempCover = jsonObject.getString("cover_url")
            tempBook = Book(tempTitle, tempAuthor, tempId, tempCover)
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