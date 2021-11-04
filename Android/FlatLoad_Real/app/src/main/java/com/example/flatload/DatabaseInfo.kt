package com.example.flatload


//import com.google.android.gms.maps.model.LatLng
import com.naver.maps.geometry.LatLng
import java.io.Serializable

data class DatabaseInfo(val INFO_ID: String, val OBSTACLE_ID: String, val latlng: LatLng, val FEATURE : String, val IMGNAME: String):
    Serializable {
}