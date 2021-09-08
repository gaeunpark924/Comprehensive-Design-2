package com.example.flatload.model

//import android.provider.Settings.Global.getString
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.flatload.FlatAPI
import com.example.flatload.R
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_input_way.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

interface DataModel {
    fun postData(body: MultipartBody.Part, map:HashMap<String, RequestBody>) : Single<String>
    fun getRouteData(origin: Point, destination: Point, context: Context)
}

class DataModelImpl (private val server: FlatAPI): DataModel{        //DataModel 인터페이스를 구현하는 클래스
    //위험요소 이미지 전송
    override fun postData(body: MultipartBody.Part, map:HashMap<String, RequestBody>) : Single<String> {
        Log.d("MYTEST","postData")
        return server.postImage(body, map)
    }

    override fun getRouteData(origin: Point, destination: Point, context: Context){

    }


}