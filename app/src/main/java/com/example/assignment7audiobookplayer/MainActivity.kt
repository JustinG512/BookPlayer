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

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/*Main Activity*/
class MainActivity : AppCompatActivity(), BookListFragment.SelectionFragmentInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bookList = BookList() // Create new bookList from class BookList
        /*Receive information from strings.xml file*/
        val titleArray: Array<String> = resources.getStringArray(R.array.book_titles)
        val authorArray: Array<String> = resources.getStringArray(R.array.book_authors)

        /*Loop through the length of the array in strings.xmla and add to bookList Title, then
        * Author, then loop until the entries are imported*/
        for (i in titleArray.indices) {
            bookList.add(Book(titleArray[i], authorArray[i]))
        }

        /*Begin with container one*/
        val fragment = supportFragmentManager.findFragmentById(R.id.container1)
        if (fragment != null)
            supportFragmentManager.beginTransaction().remove(fragment).commit()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container1, BookListFragment.newInstance(bookList))
            .commit()
    }


    /*If book is selected this will add information to container two based off of what is
    * listed in container 1*/
    override fun bookSelected() {
        if (findViewById<View>(R.id.container2) == null)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
    }
}