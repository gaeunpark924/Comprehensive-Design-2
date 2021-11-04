package com.example.flatload

//import com.google.android.gms.maps.model.LatLng
import com.naver.maps.geometry.LatLng
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RoadviewInfo(
    @SerializedName("location")
    var location:LatLng,//String,//Pair<Double, Double>,
    @SerializedName("image")
    var image:String
): Serializable {

}

