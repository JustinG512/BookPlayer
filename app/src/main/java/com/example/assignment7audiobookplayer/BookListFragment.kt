package com.example.assignment7audiobookplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/*BookList fragment*/
private const val BOOKS_KEY = "books_key"

class BookListFragment : Fragment() {
    private var books: BookList? = null
    private lateinit var bookViewModel : BookViewModel

    /*onCreate function*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)

        arguments?.let {
            books = it.getParcelable(BOOKS_KEY)
        }
    }

    /*onCreateView function*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_list, container, false) as RecyclerView
    }

    /*onViewCreated function*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with (view as RecyclerView) {

            books?.run{
                val clickEvent = {
                        book:Book -> bookViewModel.setSelectedBook(book)
                    (requireActivity() as SelectionFragmentInterface).bookSelected()
                }

                layoutManager = LinearLayoutManager(requireContext())
                adapter = BookListAdapter(this, clickEvent)
            }
        }
    }

    /*BookListAdapter function*/
    class BookListAdapter(_books: BookList, _clickEvent: (Book)->Unit) : RecyclerView.Adapter<BookListAdapter.BookListViewHolder>() {

        private val books = _books
        val clickEvent = _clickEvent

        class BookListViewHolder(_view: View) : RecyclerView.ViewHolder(_view){
            val view = _view
            val title: TextView = _view.findViewById(R.id.textView_title)
            val author: TextView = _view.findViewById(R.id.textView_author)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookListViewHolder {
            return BookListViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.book_list_layout, parent, false)
            )
        }

        override fun onBindViewHolder(holder: BookListViewHolder, position: Int) {
            holder.title.text = books[position].title
            holder.author.text = books[position].author
            holder.view.setOnClickListener { clickEvent(books[position]) }
        }

        override fun getItemCount(): Int {
            return books.size()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(list : BookList) =
            BookListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BOOKS_KEY, list)
                }
            }
    }

    interface SelectionFragmentInterface {
        fun bookSelected()
    }
}