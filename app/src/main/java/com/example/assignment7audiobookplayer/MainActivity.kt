/*Assignment Self Grading Live Tracker

[X] Application has layouts for portrait and landscape configurations 10%
[X] BookListFragment has a factory method that can create new fragment instances 10%
[X] BookListFragment has RecyclerView that shows both title and author in their own
        TextViews 10%
[X] BookListFragment properly communicates selected book using ViewModel 15%
[X] Proper master-detail implementation when in small-portrait mode (new
        BookDetailsFragment created when book is clicked which replaces BookListFragment) 15%
[X] Proper master-detail implementation when in large-portrait or landscape mode (Single
        instance of BookDetailsFragment being updated when book is clicked) 20%
[X] Selected book and fragment display is remembered across orientation changes 20%*/

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

        val searchButton = findViewById<Button>(R.id.searchButton)

        bookListVM = ViewModelProvider(this)[BookListViewModel::class.java]
        val bookViewModel: BookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        Log.d("TEST", "Main 1")

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
            Log.d("Book", "$tempTitle $tempAuthor $tempId $tempImg")
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