package com.example.flatload.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.flatload.MapActivity
import com.example.flatload.PairList
import com.example.flatload.PointIntent
import com.example.flatload.R
import com.example.flatload.base.BaseActivity
import com.example.flatload.databinding.ActivityMainBinding
import com.example.flatload.viewmodel.InputWayViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.android.synthetic.main.activity_input_way.*
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt

class InputWayActivity : BaseActivity<ActivityMainBinding, InputWayViewModel>() { //출발지 도착지 입력 화면 - gps 허용 추가

    override val layoutResourceId: Int
        get() = R.layout.activity_input_way

    override val viewModel: InputWayViewModel by viewModel("viewModelInputWayPart")


    var pairList = mutableListOf<Pair<Double,Double>>()
    lateinit var PointList:ArrayList<Point>

    lateinit var startPoint:Point
    lateinit var endPoint:Point

    override fun initStartView() {

    }
    override fun initDataBinding() {
        viewModel.startLiveData.observe(this, androidx.lifecycle.Observer{
            editTextStart.setText(it)
        })
        viewModel.endLiveData.observe(this, androidx.lifecycle.Observer {
            editText_type.setText(it)
        })
    }
    override fun initAfterBinding() {
        init()
    }

    private fun init() {
        //처음에 gps 허용
        //내위치 누르면 현재 위치로 설정
        //확인 버튼 누르면 출발지, 도착지 -> 위도 경도로 변경
        val mgeocorder: Geocoder = Geocoder(this, Locale.getDefault())
        initLocation() //gps 설정

        button3.setOnClickListener { //내 위치 버튼
            //loc.latitude,loc.longitude <- 현재 위치 위도 경도
            //위도 경도-> 텍스트 변경해서 출발지 edittext에 표시
            val txtLoc = mgeocorder.getFromLocation(loc.latitude,loc.longitude,1)[0]
            if(txtLoc.getAddressLine(0)!=null){
                editTextStart.setText(txtLoc.getAddressLine(0))
            }
            Log.i("my location", txtLoc.toString())
            //val txtloc = ReverseGeocoding(loc.longitude,loc.latitude)
        }
        button.setOnClickListener { //확인 버튼
            PointList = ArrayList<Point>()
            pairList.clear()
            PointList.clear()

            val start = editTextStart.text.toString()
            val end = editTextEnd.text.toString()

            if(start.isNotEmpty() && end.isNotEmpty()) {
                startGeocoding(start)
                endGeocoding(end)
                //Log.d("포인트리스트 확인",PointList.toString())
            }else{
                Toast.makeText(this,"위치를 입력해주세요", Toast.LENGTH_LONG).show()
                editTextStart.text.clear()
                editTextEnd.text.clear()
            }
        }
        button2.setOnClickListener { // 취소 버튼
            //startPoint endPoint pairList PointList 초기화
            PointList.clear()
            pairList.clear()
            editTextStart.text.clear()
            editTextEnd.text.clear()
            textviewJSONText.setText(" ")
        }
    }

    private fun savePointToList(point: Point){
        PointList.add(point)
        if(PointList.size == 2){
            textviewJSONText.setText(" ")
            getRoute(PointList[0], PointList[1])
        }
    }

    private fun saveStartPoint(point: Point){
        startPoint = point
    }

    private fun saveEndPoint(point: Point){
        endPoint = point
    }


    private fun startGeocoding(strlocation: String) {
        val mapboxGeocoding = MapboxGeocoding.builder()
            .accessToken(getString(R.string.access_token))
            .query(strlocation)
            .build()
        mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                val results = response.body()!!.features()
                if (results.size > 0) {
                    val firstResultPoint = results[0].center()
                    Log.d("geocoding확인", "onResponse: " + firstResultPoint!!.toString())
                    //results[0].center()?.let { saveStartPoint(it) }
                    saveStartPoint(firstResultPoint)
                    results[0].center()?.let { savePointToList(it) }
                    Log.d("포인트리스트 확인 in enqueue",PointList.toString())

                } else {
                    Log.d("geocoding확인", "onResponse: No result found")
                }
            }
            override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                throwable.printStackTrace()
            }
        })

    }

    private fun endGeocoding(strlocation: String) {
        val mapboxGeocoding = MapboxGeocoding.builder()
            .accessToken(getString(R.string.access_token))
            .query(strlocation)
            .build()
        mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                val results = response.body()!!.features()
                if (results.size > 0) {
                    val firstResultPoint = results[0].center()
                    Log.d("geocoding확인", "onResponse: " + firstResultPoint!!.toString())
                    //results[0].center()?.let { saveStartPoint(it) }
                    saveEndPoint(firstResultPoint)
                    results[0].center()?.let { savePointToList(it) }
                    Log.d("포인트리스트 확인 in enqueue",PointList.toString())
                } else {
                    Log.d("geocoding확인", "onResponse: No result found")
                }
            }
            override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                throwable.printStackTrace()
            }
        })
    }


    private fun ReverseGeocoding(longitude: Double, latitude: Double) {
        val mapboxGeocoding = MapboxGeocoding.builder()
            .accessToken(getString(R.string.access_token)).country("korea")
            .query(Point.fromLngLat(longitude,latitude))
            .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS).build()

        mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                val results = response.body()!!.features()
                if (results.size > 0) {
                    // Log the first results Point.
                    val firstResultPoint = results[0]
                    Log.d("reverse geocoding확인", "onResponse: " + firstResultPoint!!.toString())

                } else {
                    // No result for your request were found.
                    Log.d("reverse geocoding확인", "onResponse: No result found")
                }
            }
            override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                throwable.printStackTrace()
            }
        })
    }

    private fun getRoute(origin: Point, destination: Point) {
        //변수 선언
        //var pairList = mutableListOf<Pair<Double,Double>>() //전역변수로 변경함
        var flag=0
        val context: Context = this
        //viewModel.getRouteMapBox(origin, destination, context)

        //맵박스 길찾기 요청
        val client = MapboxDirections.builder() //builder 패턴 방식으로 MapboxDirections 클래스의 객체룰 생성. 변수의 순서 바뀌면 안됨
            .origin(origin) //출발지
            .destination(destination) //목적지
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_WALKING) //교통, 운전, 걷기, 사이클링
            .steps(true)
            //.geometries("geojson")
            .accessToken(getString(R.string.access_token))
            .build()

        //val response = client.executeCall().body()
        //Log.i("response", response.toString())

        pairList.clear()


        //길찾기 응답
        client?.enqueueCall(object : Callback<DirectionsResponse> {
            @SuppressLint("LogNotTimber")
            override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                if (response.body() == null) {
                    Log.i("error", "No routes found, make sure you set the right user and access token.")
                    return
                } else if (response.body()!!.routes().size < 1) {
                    Log.i("error", "No routes found")
                    return
                }
                // Get the directions route
                val currentRoute = response.body()!!.routes()[0]
                //textviewJSONText?.setText(response.body()!!.toJson())
                val jsonString = response.body()!!.toJson().trimIndent()//json 형식으로 바꿔서 string에 저장
                val jsonObject = JSONObject(jsonString)
                val jsonArray = jsonObject.getJSONArray("routes")
                val subjsonObject = jsonArray.getJSONObject(0) //route 배열의 index = 0

                // 이동거리, 소요시간 추가
                val duration = subjsonObject.getString("duration") // 초 단위
                val distance = subjsonObject.getString("distance") // m 단위

                // 단위 변경
                val duration_min = (duration.toFloat()/60.0 * 100).roundToInt() / 100f
                val distance_km = (distance.toFloat()/1000.0 * 100).roundToInt() / 100f


                val subjsonArray = subjsonObject.getJSONArray("legs")
                val subjsonObject2 = subjsonArray.getJSONObject(0)//legs 배열의 index = 0
                val subjsonArray2 = subjsonObject2.getJSONArray("steps")

                var cnt:Int = 0

                //json 파싱 intersection
                for( i in 0..subjsonArray2.length()-1){ //step배열의 index 0 부터 끝까지
                    val iObject = subjsonArray2.getJSONObject(i) //index i 의 값을 객체로 생성
                    val iArray =iObject.getJSONArray("intersections") //intersection 배열

                    for (j in 0..iArray.length()-1){
                        val jObject =iArray.getJSONObject(j)
                        val location =jObject.getJSONArray("location") //intersection 배열의 location 값을 얻어옴

                        println("${i+1}번째 intersections ${j+1}번째 location"+location)

                        val pair = Pair(location[0].toString().toDouble(), location[1].toString().toDouble())
                        pairList.add(cnt, pair)
                        cnt = cnt + 1
                    }
                }
                Log.i("이동거리,소요시간 출력", distance_km.toString() +"km, "+duration_min.toString()+"분")
                textviewJSONText?.setText(pairList.toString()) //textview로 띄움
                flag=1
                val result = checkDistance(distance_km)
                if(result == 1){
                    //sendToServer(pairList)
                    goToMap(pairList)
                }
                //goToMap(pairList)
            }
            override fun onFailure(call: Call<DirectionsResponse>, throwable: Throwable) {
                Log.i("error", "Error: " + throwable.message)
            }
        }
        )
    }

    private fun checkDistance(distanceKm: Float): Int {
        if(distanceKm >= 3.00){
            Toast.makeText(this,"해당 서비스는 3km 이내의 도보 길찾기 경로만 제공 합니다.", Toast.LENGTH_LONG).show()
            textviewJSONText.setText(" ")
            editTextStart.text.clear()
            editTextEnd.text.clear()
            return 0
        }
        else{
            return 1
        }
    }

    private fun goToMap(pairList: List<Pair<Double, Double>>){
        if(pairList.isNotEmpty()) {
            val i = Intent(this, MapActivity::class.java)
            i.putExtra("pairList", PairList(pairList))
            //i.putExtra("resultGet",ResultGetList(resultGet))
            //i.putExtra("startPoint",startPoint.toString())
            i.putExtra("start", PointIntent(startPoint))
            i.putExtra("end", PointIntent(endPoint))
            startActivity(i)
        }
    }

}