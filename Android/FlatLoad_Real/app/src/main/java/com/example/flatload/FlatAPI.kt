package com.example.flatload

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.Serializable

interface FlatAPI {
    //Mapbox API로 도보 경로 받아오기
    @GET("/android/get")
    fun getJson(): Call<List<ResultGet>>

    //앱 -> 서버로 경로 좌표 전송
    @FormUrlEncoded
    @POST("/android/post")
    fun postJson(
        @Field("locations") locations : List<Pair<Double, Double>>
    ): Call<List<ResultGet>>

    //앱 -> 서버로 위험요소 사진 전송
    @Multipart
    @POST("/android/post/upload")
    fun postImage(
        @Part photo: MultipartBody.Part,
        @PartMap data: HashMap<String, RequestBody>
    ): Single<String>

    @GET("/android/result")
    fun getResult(): Call<String>
}