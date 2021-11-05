package com.example.flatload

import android.content.ContentValues.TAG
import android.graphics.BitmapFactory
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.naver.maps.geometry.LatLng
import kotlinx.android.synthetic.main.activity_database_result.*
import kotlinx.android.synthetic.main.activity_marker_result.*
import kotlinx.android.synthetic.main.fragment_add_risk.*
import kotlinx.android.synthetic.main.fragment_input_way.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class DatabaseResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_result)
        init()
    }
    private fun init() {
        val i = intent
        val bundle = i.getParcelableExtra<Bundle>("bundle")
        val latlng = bundle.getParcelable<LatLng>("location")
        val mgeocorder: Geocoder = Geocoder(this, Locale.getDefault())

        if (latlng != null) {
            val txtLoc = mgeocorder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )[0]
            if (txtLoc.getAddressLine(0) != null) {
                textView_location.setText(txtLoc.getAddressLine(0))
            }else{
                textView_location.setText(latlng.toString())
            }
        }
//        val mgeocorder: Geocoder = Geocoder(this, Locale.getDefault())
//        val txtLoc = mgeocorder.getFromLocation(latlng!!.latitude,latlng!!.longitude,1)[0]
//
//        if(txtLoc.getAddressLine(0)!=null){
//            textView6.setText(txtLoc.getAddressLine(0))
//        }else{
//            textView6.setText("주소 x")
//        }
//        Log.i("danger location", txtLoc.toString())

        //textView6.text = latlng.toString()

        val obstacle = i.getStringExtra("obstacle")
        textView_obstacle.setText(obstacle)
        if(!(i.getStringExtra("image").isNullOrEmpty())){
            val decodedBytes = i.getByteArrayExtra("image")
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imageView.setImageBitmap(bitmap)
        }

        if(!(i.getStringExtra("feature").isNullOrEmpty())){
            val feature = i.getStringExtra("feature")
            textView_feature.setText(feature)
        }
    }

}