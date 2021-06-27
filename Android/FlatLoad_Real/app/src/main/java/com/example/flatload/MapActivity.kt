package com.example.flatload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.GsonBuilder
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.plugins.localization.LocalizationPlugin
import com.mapbox.mapboxsdk.plugins.localization.MapLocale
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MapActivity : AppCompatActivity(),PermissionsListener,OnMapReadyCallback{//,MapboxMap.OnMapClickListener {

    private var mapView: MapView? = null
    private lateinit var routeCoordinates: ArrayList<Point>
    private var permissionsManager: PermissionsManager? = null
    private var mapboxMap: MapboxMap? = null
    private lateinit var LocList: PairList

    private var symbolManager: SymbolManager? = null

    private val ICON_ID = "ICON_ID"
    private val DANGER_ICON_ID = "DANGER_ICON_ID"
    private var localizationPlugin: LocalizationPlugin? = null

    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.MINUTES)
        .build();

    val BASE_URL_FLAT_API ="http://3.36.64.251:8080" //"http://15.164.166.74:8080"(민영) //"http://10.0.2.2:3000"(에뮬레이터-로컬서버 통신)
    val gson = GsonBuilder().setLenient().create()
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_FLAT_API).client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
    //.addConverterFactory(ScalarsConverterFactory.create())
    //.build()
    val api = retrofit.create(FlatAPI::class.java)

    private lateinit var resultList: List<ResultGet> // 서버에서 받은 리스트
    private lateinit var startPoint: LatLng //출발지
    private lateinit var endPoint: LatLng //도착지

    @SuppressLint("LogNotTimber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(this, getString(R.string.access_token))

        setContentView(R.layout.activity_map)

        val i = intent
        LocList = i.getSerializableExtra("pairList") as PairList
        Log.i("pairList확인", LocList.pairList.toString())

        val start = i.getSerializableExtra("start") as PointIntent
        startPoint = LatLng(start.point.latitude(),start.point.longitude())
        Log.i("startPoint확인", startPoint.toString())

        val end = i.getSerializableExtra("end") as PointIntent
        endPoint= LatLng(end.point.latitude(),end.point.longitude())
        Log.i("endPoint확인", endPoint.toString())

//        startPoint = LatLng(LocList.pairList[0].second, LocList.pairList[0].first)
//        Log.i("startPoint확인", startPoint.toString())

        sendToServer(LocList.pairList) // 서버로 길찾기 좌표 전송 -> 결과 resultList에 저장
        mapView = findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
    }
    // 서버로 좌표 전송
    @SuppressLint("LogNotTimber")
    private fun sendToServer(pairList: List<Pair<Double, Double>>) {
        var LocList = mutableListOf<Location>()
        Log.d("pairList 확인:",pairList.toString() )

        var newpairList = mutableListOf<Pair<Double,Double>>()

        for (i in 0..pairList.size-1){
            val pair = pairList[i]
            val first = pairList[i].first
            val second = pairList[i].second
            newpairList.add(Pair(second,first))
        }
        Log.d("newpairList 확인:",newpairList.toString())

        val callPostJson = api.postJson(newpairList)
        val thread = Thread.sleep(10000)
        thread.run {

        }
        callPostJson.enqueue(object : Callback<List<ResultGet>> {
            override fun onFailure(call: Call<List<ResultGet>>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
                makeToast("실패 : $t")
            }
            override fun onResponse(
                call: Call<List<ResultGet>>,
                response: Response<List<ResultGet>>
            ) {
                Log.d("결과", "성공 : ${response.raw()}")
                Log.d("출력", "성공 :" + response?.body().toString())
                //resultMsgFromServer(response?.body().toString())
                makeToast(response?.body().toString())
                if(!response?.body().toString().contains("null")){ // 위험요소가 없으면
                    saveResultGet(response?.body())
                }
            }
        })
    }
    @SuppressLint("LogNotTimber")
    private fun saveResultGet(body: List<ResultGet>?) {
        if (body != null) {
            resultList = body
            initRouteCoordinates(LocList)
            Log.i("resultList 확인:",resultList.toString())
            mapView?.getMapAsync(this) //onMapReadyCallback 시작
        }
   }

    private fun checkClickedSymbol(latLng: LatLng): Int {
        if (resultList.size > 1) {
            for (i in 0..resultList.size - 1) {
                if (LatLng(resultList[i].location[1], resultList[i].location[0]) == latLng) {
                    Toast.makeText(
                        this@MapActivity,
                        i.toString() + "번째 데이터와 동일", Toast.LENGTH_SHORT
                    ).show()
                    changeActivity(latLng, resultList[i].image)
                }
            }
        } else if (resultList.size == 1) { //위험요소가 하나면
            if (LatLng(resultList[0].location[1], resultList[0].location[0]) == latLng) {
                Toast.makeText(
                    this@MapActivity,
                    "0번째 데이터와 동일", Toast.LENGTH_SHORT
                ).show()
                changeActivity(latLng, resultList[0].image)
            }
        }
        return 1
    }

    private fun makeToast(toString: String) {
        Toast.makeText(this,toString, Toast.LENGTH_LONG).show()
    }

    private fun initRouteCoordinates(locList: PairList) {
        //routeCoordinates: ArrayList<Point>? = null
        Log.i("확인", locList.toString())
        Log.i("확인", locList.pairList.size.toString())
        Log.i("확인", locList.pairList[0].first.toString())
        Log.i("확인", locList.pairList[0].second.toString())
        routeCoordinates = ArrayList<Point>()

        for (i in 0..locList.pairList.size-1){
            routeCoordinates.add(Point.fromLngLat(locList.pairList[i].first,locList.pairList[i].second))
            //Log.i("LocList 확인:", routeCoordinates.get(i).toString())
        }
    }

    @SuppressLint("LogNotTimber")
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        val resultGetList = this.resultList
        val loclist = this.LocList.pairList

        Log.d("onMapReady 안에서 resultGetList 출력:",resultGetList.toString())
        Log.d("onMapReady 안에서 startPoint 출력:",startPoint.toString())

        /* ==================== 카메라 target =============== */
        val position = CameraPosition.Builder()
            .target(startPoint) //출발지로 바꾸기
            .zoom(16.0)
            .tilt(60.00)
            .build()
        mapboxMap.cameraPosition = position

        mapboxMap?.setStyle(Style.MAPBOX_STREETS, object: Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                // 마커 이미지 변경
                style.addImage(ICON_ID, BitmapFactory.decodeResource(this@MapActivity.resources,R.drawable.mapbox_marker_icon_default))
                style.addImage(DANGER_ICON_ID, BitmapFactory.decodeResource(this@MapActivity.resources,R.drawable.danger))

                Log.i("onMapReady 안에서 endPoint 확인:",endPoint.toString())

                // 언어 설정
                localizationPlugin = LocalizationPlugin(mapView!!, mapboxMap, style)
                localizationPlugin!!.setMapLanguage(MapLocale.KOREA)

                // 마커 추가
                symbolManager = SymbolManager(mapView!!, mapboxMap, style)
                val destinationSymbol = symbolManager!!.create(SymbolOptions().withLatLng(endPoint).withIconImage(ICON_ID).withIconSize(1.7f))
                symbolManager!!.iconAllowOverlap = true
                symbolManager!!.textAllowOverlap = true

                if(resultGetList.size > 1){ // 서버에서 받은 결과가 2이상이면 (위험요소 좌표,이미지 리스트)
                    for(i in 0..resultGetList.size-1){
                        val reuseSymbolManager = SymbolManager(mapView!!, mapboxMap, style)
                        reuseSymbolManager!!.iconAllowOverlap = true
                        reuseSymbolManager!!.textAllowOverlap = true

                        //================== 위험요소 마커 추가 ======================//
                        val symbol = reuseSymbolManager!!.create(
                            SymbolOptions()
                                .withLatLng(LatLng(resultGetList[i].location[1],resultGetList[i].location[0]))
                                //.withLatLng(LatLng(37.547147, 127.074148))
                                .withIconImage(DANGER_ICON_ID)
                                .withIconSize(0.06f)
                        )

                        //symbol.textField = " 위험 요소 "
                        reuseSymbolManager!!.addClickListener(object : OnSymbolClickListener {
                            override fun onAnnotationClick(symbol: Symbol): Boolean {
                                Toast.makeText(
                                    this@MapActivity,
                                    "click marker symbol", Toast.LENGTH_SHORT
                                ).show()
                                //클릭된 심볼이 ressultGetList에 있으면 좌표,이미지 인텐트로 넘기기기 - 함수로
                                val results = checkClickedSymbol(symbol.latLng)
                                //symbol.iconImage = MAKI_ICON_CAFE
                                //symbolManager!!.update(symbol)
                                //changeActivity(symbol.latLng,resultGetList[0].image)
                                return true
                            }
                        })
                    }
                } else if(resultGetList.size == 1) {
                    val reuseSymbolManager2 = SymbolManager(mapView!!, mapboxMap, style)
                    //================== 위험요소 마커 추가 ======================//
                    val symbol2 = reuseSymbolManager2.create(
                        SymbolOptions()
                            .withLatLng(LatLng(resultGetList[0].location[1],resultGetList[0].location[0]))
                            //.withLatLng(LatLng(37.547147, 127.074148)) //위험 요소 마커 추가
                            .withIconImage(DANGER_ICON_ID)
                            .withIconSize(0.06f)
                    )
                    reuseSymbolManager2!!.addClickListener(object : OnSymbolClickListener {
                        override fun onAnnotationClick(symbol: Symbol): Boolean {
                            Toast.makeText(
                                this@MapActivity,
                                "click marker symbol", Toast.LENGTH_SHORT
                            ).show()
                            //클릭된 심볼이 ressultGetList에 있으면 좌표,이미지 인텐트로 넘기기기 - 함수로
                            val results = checkClickedSymbol(symbol.latLng)
                            return true
                        }
                    })
                }
            enableLocationComponent(style)
            // 길찾기 polyline 추가
            style.addSource(
            GeoJsonSource("line-source",
            FeatureCollection.fromFeatures(arrayOf(
                            Feature.fromGeometry(
                                LineString.fromLngLats(routeCoordinates!!)
                            ))))
                )
                style.addLayer(
                    LineLayer("linelayer", "line-source").withProperties(
                        PropertyFactory.lineDasharray(arrayOf(0.01f, 2f)),
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                        PropertyFactory.lineWidth(10f),
                        PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
                    ))
                //mapboxMap.addOnMapClickListener(this@MapActivity)
            }
        })
    }

    private fun changeActivity(location: LatLng, imgstr:String) {
        val i = Intent(this,MarkerResultActivity::class.java)
//        val latlngintent = LatlngIntent(location)
//        i.putExtra("markerLocation", latlngintent)
//        i.putExtra("imageString",imgstr)

        val args = Bundle()
        args.putParcelable("markerLocation",location)
        i.putExtra("bundle", args)

        val decodedBytes = Base64.decode(imgstr,0)
        //val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        i.putExtra("image",decodedBytes)
        startActivity(i)
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(this, "user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxMap!!.getStyle { style -> enableLocationComponent(style) }
        } else {
            Toast.makeText(this, "user_location_permission_not_granted", Toast.LENGTH_LONG)
                .show()
            finish()
        }
    }

    @SuppressWarnings( "MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            val customLocationComponentOptions =
                LocationComponentOptions.builder(this)
                    .pulseEnabled(true).bearingTintColor(Color.BLUE).backgroundTintColor(Color.BLUE)
                    .build()

        // Get an instance of the component
            val locationComponent = mapboxMap!!.locationComponent

        // Activate with options
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()
            )

        // Enable to make component visible
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
            locationComponent.isLocationComponentEnabled = true

            //locationComponent.cameraMode = CameraMode.TRACKING

            locationComponent.renderMode = RenderMode.COMPASS
            //initLocationEngine()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager!!.requestLocationPermissions(this)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState!!)
        mapView!!.onSaveInstanceState(outState)
    }


}

