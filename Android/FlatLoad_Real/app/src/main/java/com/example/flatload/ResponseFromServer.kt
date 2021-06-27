package com.example.flatload
import com.google.gson.annotations.SerializedName

data class ResponseFromServer (
    @SerializedName("result")
    var result:String? = null
)
