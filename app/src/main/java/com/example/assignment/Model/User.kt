package com.example.assignment.Model

import android.os.Parcel
import android.os.Parcelable

data class User(
//    val id: String,
    val id: String,
    val imageUrl: String,
    val name: String,
    val email: String,
    val contactNo: String,
    val gender: String,
    val password: String,
)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(contactNo)
        parcel.writeString(gender)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User? {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
