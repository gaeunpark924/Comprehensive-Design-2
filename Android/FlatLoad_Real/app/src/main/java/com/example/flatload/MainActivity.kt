package com.example.flatload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val fragmentCommunity by lazy { CommunityFragment() }
    private val fragmentInputWay by lazy { InputWayFragment() }
    private val fragmentMap by lazy { MapFragment() }
    private val fragmentAddRisk by lazy { AddRiskFragment() }
    //private val fragmentRocalSearch by lazy{ RocalSearchFragment()}

    var fusedLocationClient: FusedLocationProviderClient?= null
    var loc= LatLng(0.0,0.0)
    var locationCallback: LocationCallback?=null
    var locationRequest: LocationRequest?=null

    var manager = supportFragmentManager
    var active: Fragment = fragmentMap
    //    var itemList = mutableListOf<ItemList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val localAdapter = ListViewAdapter(itemList)
//        listView.adapter = localAdapter
        //supportFragmentManager.beginTransaction().replace(R.id.fl_container,fragmentMap).commit();
        //changeFragment(fragmentMap)
        init()
        initLocation()

    }
    override fun onCreateOptionsMenu(menu: Menu):Boolean{
        super.onCreateOptionsMenu(menu)
        //getMenuInflater().inflate(R.menu.menu_top,menu)
        setFragment(fragmentMap,"1")
        return true
    }
//    override fun onOptionsItemSelected(item: MenuItem):Boolean{
//        super.onOptionsItemSelected(item)
//        when (item.itemId) {
//            R.id.five ->{
//                setFragment(fragmentRocalSearch,"1")
//            }
//        }
//        return true
//    }
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
            getuserlocation() //현재위치 갱신
            startLocationUpdates() //업데이트
        }
        else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),100)
            //처음엔 권한 요청함
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
                .addOnSuccessListener {//성공적으로 위치 가져왔으면?
                    if (it == null) {
                        Log.i("위치 가져오기 실패", "")    //현재 위치를 바로 가져올 수 없을 때 예외처리
                    } else {
                        loc = LatLng(it.latitude, it.longitude)  //현재위치로 위치정보를 바꾸겠다
                        Log.i("currentLocation", loc.toString())
                    }
                }
                .addOnFailureListener{
                    Log.i("location error","")          //
                }
        }
//        fusedLocationClient?.lastLocation?.addOnSuccessListener {//성공적으로 위치 가져왔으면?
//            loc = LatLng(it.latitude,it.longitude) //현재위치로 위치정보를 바꾸겠다
//            Log.i("currentLocation",loc.toString())
//        }
    }

    private fun startLocationUpdates() { //gps 관련
        locationRequest = LocationRequest.create()?.apply {
            interval= 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                //성공적으로 위치정보 업데이트 되었으면? 그 위치 정보 가져옴
                locationResult ?: return
                for(location in locationResult.locations){
                    loc= LatLng(location.latitude,location.longitude)
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
            locationCallback, //갱신되면 이함수 호출
            Looper.getMainLooper()) //메인쓰레드가 가지고있는 루퍼 객체 사용하겠다*/
    }

    override fun onRequestPermissionsResult( //권한요청하고 결과 여기로 옴
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==100){ //허용받았으면
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED){ //둘다 허용되면
                getuserlocation()
                startLocationUpdates()
            }
            else{//허용안해줬으면 기본 맵으로
                Toast.makeText(this,"위치정보 제공을 하셔야 합니다", Toast.LENGTH_SHORT).show()
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

}