package com.example.assignment7audiobookplayer

import android.os.Parcel
import android.os.Parcelable

/*Standard parcelable class to populate via Book */
class BookList(): Parcelable {

    private val list : ArrayList<Book> = ArrayList()

    constructor(parcel: Parcel) : this() {
        parcel.writeList(list)
    }

    fun add(_book : Book){
        list.add(_book)
    }

    /*This is not used but should be kept for the assignment to be complete at a
    * possible later date*/
//    fun remove(_book : Book){
//        list.remove(_book)
//    }

    operator fun get(x : Int) : Book{
        return list[x]
    }

    fun size() : Int{
        return list.size
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