package com.example.flatload

import android.os.Parcel
import android.os.Parcelable
import com.mapbox.mapboxsdk.geometry.LatLng
import java.io.Serializable

data class LatlngIntent(val latlng:LatLng): Serializable {

}