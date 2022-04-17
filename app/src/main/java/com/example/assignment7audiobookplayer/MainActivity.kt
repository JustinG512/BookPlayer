/*Assignment Self Grading Live Tracker

[X] Integrated AudioBookService 10%
[X] Properly integrate controls (Play, Stop, Pause) in ControlFragment with Now Playing label 20%
[X] SeekBar progress is associated with book duration 10%
[X] SeekBar updates as book plays to show current progress 10%
[X] SeekBar controls book progress when user moves progress bar 20%
[X] Book continues playing when activity is restarted 20%
[X] Controls continue to function and Now Playing shows correct book when activity is restarted 10% */



package com.example.assignment7audiobookplayer

import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.temple.audlibplayer.PlayerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

/*Begin Main Activity*/
class MainActivity : AppCompatActivity(), BookListFragment.SelectionFragmentInterface,
    ControlFragment.ControlFragmentInterface {

    /*Initialize bool variables*/
    var isConnected: Boolean = false
    private var once: Boolean = false

    /*Bring in my fragments and classes.  These will be developed later on*/
    private lateinit var bookListVM: BookListViewModel
    private lateinit var bookVM: BookViewModel
    lateinit var audioBinder: PlayerService.MediaControlBinder
    private lateinit var controlFrag: ControlFragment

    /*onCreate standard implantation*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*Bring in my fragments and classes.  These will be developed later on*/
        val searchButton = findViewById<Button>(R.id.button_Search)
        bookListVM = ViewModelProvider(this)[BookListViewModel::class.java]


        bookVM = ViewModelProvider(this)[BookViewModel::class.java]

        /*Search Button*/
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
        if (bookVM.getSelectedBook().value != null && findViewById<View>(R.id.container2) == null) {
            bookSelected()
        }

        bindService(
            Intent(this, PlayerService::class.java), serviceConnection, BIND_AUTO_CREATE
        )

        controlFrag = supportFragmentManager.findFragmentById(R.id.container3) as ControlFragment


//        val bookViewModel: BookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        // This will open the search dialog.  This code is very similar to what was
        // done in class during the demo.
        searchButton.setOnClickListener {
            onSearchRequested()
            if (supportFragmentManager.backStackEntryCount > 0)
                supportFragmentManager.popBackStack()
        }


    }

    /*When book is selected, this will compile the details in the container.*/
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
        var jsonObjectID: JSONObject
        var tempTitle: String //"title":"The Island of Doctor Moreau"
        var tempAuthor: String //"author":"H. G. Wells
        var tempId: Int //"id":"2"
        var tempDuration: Int // "duration":"764"
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


        /*This will sort through the incoming json data until it is empty.  Results will be
        returned accordingly*/
        for (i in 0 until jsonArray.length()) {
            jsonObject = jsonArray.getJSONObject(i)
            tempTitle = jsonObject.getString("title")
            tempAuthor = jsonObject.getString("author")
            tempId = jsonObject.getInt("id")
            tempCover = jsonObject.getString("cover_url")

            withContext(Dispatchers.IO) {
                jsonObjectID = JSONObject(
                    URL("https://kamorris.com/lab/cis3515/book.php?id=$tempId")
                        .openStream()
                        .bufferedReader()
                        .readLine()
                )
            }

            tempDuration = jsonObjectID.getInt("duration")


            tempBook = Book(tempTitle, tempAuthor, tempId, tempCover, tempDuration)
            tempBookList.add(tempBook)
            Log.d(
                "New Book:",
                "Ti:$tempTitle Au:$tempAuthor Id:$tempId Dur:$tempDuration Cov:$tempCover"
            )
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

    override fun playCurrentBook(bookId: Int, progress: Int) {
        if (isConnected) {
            if (progress < 1)
                audioBinder.play(bookId)
            else {
                Log.d("Book Status", "Another book is already playing")
            }
        } else
            Log.d("Service", "The service is not connected.")
    }

    override fun stopCurrentBook() {
        if (isConnected) {
            audioBinder.stop()
        }
    }

    override fun pauseCurrentBook() {
        if (isConnected) {
            audioBinder.pause()
        }
    }

    override fun onStop() {
        val intent = Intent(this, PlayerService::class.java)
        this.startService(intent)
        super.onStop()
    }

    override fun seekBook(Position: Int) {
        // Seekbar is passed the current progress of the book as progress
        if (isConnected) {
            audioBinder.seekTo(Position)
        }
    }

    private var bookProgress: PlayerService.BookProgress? = null


    val progressHandler = Handler(Looper.getMainLooper()) {
        bookProgress = it.obj as? PlayerService.BookProgress
        if (bookProgress?.progress != null && this::controlFrag.isInitialized)
            Log.d("bookProgress?.progress", "+")
            controlFrag.getProgress(bookProgress!!.progress)

        if (audioBinder.isPlaying && once && bookProgress?.progress != null && this@MainActivity::controlFrag.isInitialized) {
            CoroutineScope(Dispatchers.Main).launch {
                updateControlFragment(bookProgress!!.bookId)
                Log.d("updateControlFragment(bookProgress!!.bookId)", "+")

            }
        }

        true
    }

    private suspend fun updateControlFragment(bookId: Int) {
        Log.d("NewTest", "updateControlFragment")
        val tempBook: Book
        withContext(Dispatchers.IO) {
            val jsonObject = JSONObject(
                URL("https://kamorris.com/lab/cis3515/book.php?id=$bookId")
                    .openStream()
                    .bufferedReader()
                    .readLine()
            )
            tempBook = Book(
                jsonObject.getString("title"),
                jsonObject.getString("author"),
                jsonObject.getInt("id"),
                jsonObject.getString("cover_url"),
                jsonObject.getInt("duration")
            )
        }
        bookVM.setSelectedBook(tempBook)
        once = false
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isConnected = true
            audioBinder = service as PlayerService.MediaControlBinder
            audioBinder.setProgressHandler(progressHandler)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }


}