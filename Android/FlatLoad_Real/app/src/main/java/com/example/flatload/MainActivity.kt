package com.example.flatload

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.naver.maps.map.overlay.LocationOverlay
import kotlinx.android.synthetic.main.activity_main.*
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {
    private val fragmentCommunity by lazy { CommunityFragment() }
    private val fragmentInputWay by lazy { InputWayFragment() }
    private val fragmentMap by lazy { MapFragment() }
    private val fragmentAddRisk by lazy { AddRiskFragment() }

    var fusedLocationClient: FusedLocationProviderClient?= null
    var loc= LatLng(0.0,0.0)
    var locationCallback: LocationCallback?=null
    var locationRequest: LocationRequest?=null

    var manager = supportFragmentManager
    var active: Fragment = fragmentMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getAppKeyHash()
        init()
        initLocation()

    }
    override fun onCreateOptionsMenu(menu: Menu):Boolean{
        super.onCreateOptionsMenu(menu)
        setFragment(fragmentMap,"1")
        return true
    }

    private fun init() {
        bnv_main.run {
            setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.first -> {
                        setFragment(fragmentMap,"1")
                    }
                    R.id.second -> {
                        setFragment(fragmentInputWay,"2")
                    }
                    R.id.third -> {
                        setFragment(fragmentCommunity, "3")
                    }
                    R.id.four -> {
                        setFragment(fragmentAddRisk, "4")
                    }
                }
                true
            }
            selectedItemId = R.id.first
        }
    }
    fun initLocation() {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
        {
            getuserlocation() //???????????? ??????
            startLocationUpdates() //????????????
        }
        else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),100)
            //????????? ?????? ?????????
        }
    }
    private fun getuserlocation() {
        fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val temp = fusedLocationClient
        if(temp != null){
            temp.lastLocation
                .addOnSuccessListener {//??????????????? ?????? ????????????????
                    if (it == null) {
                        Log.i("?????? ???????????? ??????", "")    //?????? ????????? ?????? ????????? ??? ?????? ??? ????????????
                    } else {
                        loc = LatLng(it.latitude, it.longitude)  //??????????????? ??????????????? ????????????
                        Log.i("currentLocation", loc.toString())
                    }
                }
                .addOnFailureListener{
                    Log.i("location error","")          //
                }
        }
//        fusedLocationClient?.lastLocation?.addOnSuccessListener {//??????????????? ?????? ????????????????
//            loc = LatLng(it.latitude,it.longitude) //??????????????? ??????????????? ????????????
//            Log.i("currentLocation",loc.toString())
//        }
    }

    private fun startLocationUpdates() { //gps ??????
        locationRequest = LocationRequest.create()?.apply {
            interval= 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                //??????????????? ???????????? ???????????? ????????????? ??? ?????? ?????? ?????????
                locationResult ?: return
                for(location in locationResult.locations){
                    loc= LatLng(location.latitude,location.longitude)
                    //if(this::locationOverlay.isInitialized)
                    Log.i("changeLocation",loc.toString())
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback, //???????????? ????????? ??????
            Looper.getMainLooper()) //?????????????????? ??????????????? ?????? ?????? ???????????????*/
    }

    override fun onRequestPermissionsResult( //?????????????????? ?????? ????????? ???
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==100){ //??????????????????
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){ //?????? ????????????
                getuserlocation()
                startLocationUpdates()
            }
            else{//????????????????????? ?????? ?????????
                Toast.makeText(this,"???????????? ????????? ????????? ?????????", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun setFragment(fragment: Fragment, tag:String){
        if (fragment.isAdded()){
            manager.beginTransaction().hide(active).show(fragment).commitAllowingStateLoss()  //commit()
        }else{
            manager.beginTransaction().hide(active).add(R.id.fl_container, fragment).commitAllowingStateLoss()  //commit()
        }
        bnv_main.menu.getItem(tag.toInt()-1).setChecked(true)
        active = fragment
    }
    private fun getAppKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something: String = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key", something)
            }
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString())
        }
    }

}