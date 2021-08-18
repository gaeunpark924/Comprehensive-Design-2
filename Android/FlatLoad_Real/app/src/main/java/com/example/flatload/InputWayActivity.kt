package com.example.flatload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import kotlinx.android.synthetic.main.activity_input_way.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.roundToInt

class InputWayActivity : AppCompatActivity() { //출발지 도착지 입력 화면 - gps 허용 추가

    var fusedLocationClient: FusedLocationProviderClient?= null
    var loc= LatLng(0.0,0.0)
    var locationCallback: LocationCallback?=null
    var locationRequest: LocationRequest?=null

    var pairList = mutableListOf<Pair<Double,Double>>()
    lateinit var PointList:ArrayList<Point>

    lateinit var startPoint:Point
    lateinit var endPoint:Point

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_way)
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
            i.putExtra("start",PointIntent(startPoint))
            i.putExtra("end",PointIntent(endPoint))
            startActivity(i)
        }
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

    private fun initLocation() {
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
    }

    override fun onRequestPermissionsResult( //권한요청하고 결과 여기로 옴
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
}

