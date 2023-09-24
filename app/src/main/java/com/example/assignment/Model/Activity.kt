package com.example.assignment.Model

import android.os.Parcel
import android.os.Parcelable

data class Activity(
    val id: String,
    val name: String,
    val status: String,
    val description: String,
    val date: String,
    val totalDonationReceived: String,
    val totalRequired: String,
    val userId: String
)
    : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
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
        parcel.writeString(name)
        parcel.writeString(status)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(totalDonationReceived)
        parcel.writeString(totalRequired)
        parcel.writeString(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Notification> {
        override fun createFromParcel(parcel: Parcel): Notification {
            return Notification(parcel)
        }

        override fun newArray(size: Int): Array<Notification?> {
            return arrayOfNulls(size)
        }
    }
}
