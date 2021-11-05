package com.example.flatload

import android.graphics.BitmapFactory
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.naver.maps.geometry.LatLng
import kotlinx.android.synthetic.main.activity_database_result.*
import kotlinx.android.synthetic.main.activity_marker_result.*
import java.util.*

class MarkerResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_result)
        init()
    }
    private fun init() {
        val i = intent
//        val markerLoc = i.getSerializableExtra("markerLocation") as LatlngIntent
//        val latlng = markerLoc.latlng

        val bundle = i.getParcelableExtra<Bundle>("bundle")
        val latlng = bundle.getParcelable<LatLng>("location")
        Log.d("latlng",latlng.toString())

        //val txtLoc = mgeocorder.getFromLocation(latlng!!.latitude,latlng!!.longitude,1)[0]

//        if(txtLoc.getAddressLine(0)!=null){
//            textView6.setText(txtLoc.getAddressLine(0))
//        }else{
//            textView6.setText("주소 x")
//        }
//        Log.i("danger location", txtLoc.toString())
        val mgeocorder: Geocoder = Geocoder(this, Locale.getDefault())

        if (latlng != null) {
            val txtLoc = mgeocorder.getFromLocation(
                latlng.latitude,
                latlng.longitude,
                1
            )[0]
            if (txtLoc.getAddressLine(0) != null) {
                textView6.setText(txtLoc.getAddressLine(0))
            }else{
                textView6.setText(latlng.toString())
            }
        }
        //textView6.text = latlng.toString()
        val decodedBytes = i.getByteArrayExtra("image")
        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        imageView3.setImageBitmap(bitmap)

//        val imagestr = i.getStringExtra("imageString")
//        val decodedBytes = Base64.decode(imagestr, 0)
//        val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        //imageView3.setImageBitmap(bitmap)
    }
}