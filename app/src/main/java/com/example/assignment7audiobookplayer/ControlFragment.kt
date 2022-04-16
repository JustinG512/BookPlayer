package com.example.assignment7audiobookplayer

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.util.*


class ControlFragment : Fragment() {

    private lateinit var bookViewModel: BookViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookViewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playButton = view.findViewById<ImageButton>(R.id.imageButton_play)
        val pauseButton = view.findViewById<ImageButton>(R.id.imageButton_pause)
        val stopButton = view.findViewById<ImageButton>(R.id.imageButton_stop)
        val nowPlayingText = view.findViewById<TextView>(R.id.nowPlayingBook)
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)

        stopButton.setOnClickListener {
            (requireActivity() as ControlFragment.ControlFragmentInterface).stopCurrentBook()
//            seekBar.progress = 0
        }
        pauseButton.setOnClickListener {
            (requireActivity() as ControlFragment.ControlFragmentInterface).pauseCurrentBook()
        }
        seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "Slider is moving to = $progress");
            }

            override fun onStartTrackingTouch(seek: SeekBar?) {
            }

            override fun onStopTrackingTouch(seek: SeekBar?) {
                (requireActivity() as ControlFragment.ControlFragmentInterface).seekBook(seek!!.progress)

            }
        })



        bookViewModel.getSelectedBook().observe(requireActivity()) {
            val tempBook = it
            seekBar.max = it.duration
            seekBar.progress = 0

            playButton.setOnClickListener {
                (requireActivity() as ControlFragment.ControlFragmentInterface).playCurrentBook(
                    tempBook.id,
                    seekBar.progress
                )
                nowPlayingText.text = tempBook.title
            }

        }


    }

    public fun getProgress(progress: Int) {
        requireActivity().findViewById<SeekBar>(R.id.seekBar).progress = progress
    }

    interface ControlFragmentInterface {
        fun playCurrentBook(bookId: Int, progress: Int)
        fun stopCurrentBook()
        fun pauseCurrentBook()
        fun seekBook(Position: Int)
    }

}