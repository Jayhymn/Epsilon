package com.jayhymn.smartchat.appObjects

import android.os.Parcel
import android.os.Parcelable

data class ContactChats(var uid: String, var name: String, var phone: String):
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(name)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactChats> {
        override fun createFromParcel(parcel: Parcel): ContactChats {
            return ContactChats(parcel)
        }

        override fun newArray(size: Int): Array<ContactChats?> {
            return arrayOfNulls(size)
        }
    }

}