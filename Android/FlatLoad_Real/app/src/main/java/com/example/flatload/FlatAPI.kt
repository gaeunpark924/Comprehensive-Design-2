package com.example.flatload

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
//import org.json.JSONObject

import retrofit2.Call
import retrofit2.http.*
import java.io.Serializable
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray
//import org.json.JSONObject
import com.google.gson.JsonObject
interface FlatAPI {
    @GET("/android/get")
    fun getJson(): Call<List<ResultGet>>

    @FormUrlEncoded
    @POST("/android/post")
    fun postJson(
        @Field("locations") locations : List<Pair<Double, Double>>
    ): Call<List<ResultGet>>

    @FormUrlEncoded
    @POST("/android/post/point")
    fun postPoint(
        @Field("start") start: String,
        @Field("destination") destination: String,
        @Field("option") routeOption: String
        //@Body point : String
//        @Field("start") start : String,
//        @Field("destination") destination : String
        //@FieldMap data: HashMap<String, LatLng>
    ): Call<Array<Array<JsonObject>>>//Call<List<ResultGet>>

    //앱 -> 서버로 위험요소 사진 전송
    @Multipart
    @POST("/android/post/upload")
    fun postImage(
        @Part photo: MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    ): Call<String>

    @GET("/android/result")
    fun getResult(): Call<String>

    @GET("/v1/search/local.json")
    fun getSearchRocal(
        @Header("X-Naver-Client-Id") id :String,
        @Header("X-Naver-Client-Secret") pw :String,
        //@QueryMap query :HashMap<String, RequestBody>
        @QueryMap query :HashMap<String, String>
    ): Call<String>

    @FormUrlEncoded
    @POST("/android/post/map/coordinate")
    fun postMapCoord(
        @Field("one") one: String,
        @Field("two") two: String,
        @Field("three") three: String,
        @Field("four") four: String
    ): Call<String>
}