package com.example.flatload

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64

import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_test_get_server.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class TestGetServerActivity : AppCompatActivity() {

    val BASE_URL_FLAT_API = "http://10.0.2.2:3000"//"http://10.0.2.2:3000" //"http://15.164.166.74:8080"
    val gson = GsonBuilder().setLenient().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_get_server)
        init()
    }

    private fun init() {
        button4.setOnClickListener {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL_FLAT_API)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            val api = retrofit.create(FlatAPI::class.java)
            val callGetJson = api.getJson()
            callGetJson.enqueue(object : Callback<List<ResultGet>> {//Callback<List<ResultGet>> {
                override fun onFailure(call: Call<List<ResultGet>>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }

                override fun onResponse(
                    call: Call<List<ResultGet>>,
                    response: Response<List<ResultGet>>
                ) {
                    Log.d("결과", "성공 : ${response.raw()}")
                    //textView5.text = response.body()?.toString()

                    //val byte = response.body()?.get(0)?.location?.byteInputStream()

                    val decodedBytes = Base64.decode(response.body()?.get(0)?.image, 0)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imageView.setImageBitmap(bitmap)
                    textView5.text =response.body()?.get(0)?.location.toString()

                    textView7.text = response.body()?.get(1)?.location.toString()
                    val decodedBytes2 = Base64.decode(response.body()?.get(1)?.image, 0)
                    val bitmap2 = BitmapFactory.decodeByteArray(decodedBytes2, 0, decodedBytes2.size)
                    imageView2.setImageBitmap(bitmap2)

                    //val byte = response?.body()?.byteInputStream()
                    //val bitmap = BitmapFactory.decodeStream(byte)
                    //imageView.setImageBitmap(bitmap)
                    Log.d("출력", "성공 :" + response.body().toString())
                }
            })

        }
    }
}
