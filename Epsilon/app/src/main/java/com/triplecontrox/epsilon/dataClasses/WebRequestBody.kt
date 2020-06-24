package com.triplecontrox.epsilon.dataClasses

import android.location.Location
import android.os.Parcel
import android.os.Parcelable

data class WebRequestBody(
    val Id: Int =0,
    val location: Location?,
    val siteName: String?,
    val clockedIn: String?,
    val assignee: String?,
    val table: String?,
    val url: String?,
    val verifyCount: Int = 0,
    val shouldVerify: Int = 0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Location::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(Id)
        parcel.writeParcelable(location, flags)
        parcel.writeString(siteName)
        parcel.writeString(clockedIn)
        parcel.writeString(assignee)
        parcel.writeString(table)
        parcel.writeString(url)
        parcel.writeInt(verifyCount)
        parcel.writeInt(shouldVerify)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WebRequestBody> {
        override fun createFromParcel(parcel: Parcel): WebRequestBody {
            return WebRequestBody(parcel)
        }

        override fun newArray(size: Int): Array<WebRequestBody?> {
            return arrayOfNulls(size)
        }
    }
}