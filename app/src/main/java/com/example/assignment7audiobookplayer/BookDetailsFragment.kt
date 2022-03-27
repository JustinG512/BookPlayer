package com.example.assignment7audiobookplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class BookDetailsFragment : Fragment() {

    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
    }

    /*OnCreate this will inflate the previously created fragment_book_details and push to
    * screen*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_book_details, container, false)
    }

    /*OnViewCreated will use observe and push the data to the second fragment */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookViewModel.getSelectedBook().observe(requireActivity()) {
            val title = view.findViewById<TextView>(R.id.textView_title2)
            val author = view.findViewById<TextView>(R.id.textView_author2)
            title.text = it.title
            author.text = it.author
        }

    }

}