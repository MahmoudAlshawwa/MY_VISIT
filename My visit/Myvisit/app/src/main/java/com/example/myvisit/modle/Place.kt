package com.example.myvisit.modle

import android.os.Parcel
import android.os.Parcelable

data class Place(
    var id: String?, var name: String?, var address: String?,
    var imageplace: String, val locationLat: Double, val locationLong: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(

        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString().toString(),
        parcel.readDouble(),
        parcel.readDouble()


    ) {
    }

      constructor() : this("", "", "", "", 0.0, 0.0)

    //constructor(name: String,address: String,imageplace: String):this("",name,address,imageplace.toString())
    constructor(name: String, address: String, imageplace: String, locationLat: Double,
        locationLong: Double) : this("", name, address, imageplace.toString(), locationLat, locationLong)

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
        dest.writeString(address)
        dest.writeString(imageplace)
        dest.writeDouble(locationLat)
        dest.writeDouble(locationLong)


    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }
}
