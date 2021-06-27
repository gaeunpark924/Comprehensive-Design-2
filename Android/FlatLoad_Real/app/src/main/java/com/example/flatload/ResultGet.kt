package com.example.flatload

import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Point
import java.io.Serializable

data class ResultGet(/* 서버에서 받는 json data class */
    @SerializedName("location")
    var location:List<Double>,//String,//Pair<Double, Double>,
    @SerializedName("image")
    var image:String
): Serializable
