package com.example.assignment7audiobookplayer

import android.os.Parcel
import android.os.Parcelable

class BookList (): Parcelable {

    private val list : ArrayList<Book> = ArrayList<Book>()

    constructor(parcel: Parcel) : this() {
        parcel.writeList(list)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookList> {
        override fun createFromParcel(parcel: Parcel): BookList {
            return BookList(parcel)
        }

        override fun newArray(size: Int): Array<BookList?> {
            return arrayOfNulls(size)
        }
    }
}