package com.example.flatload

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.os.Looper
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.kakao.sdk.newtoneapi.SpeechRecognizerManager
import com.kakao.sdk.newtoneapi.TextToSpeechClient
import com.kakao.sdk.newtoneapi.TextToSpeechListener
import com.kakao.sdk.newtoneapi.TextToSpeechManager
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.fragment_input_way.*
import kotlinx.android.synthetic.main.fragment_map.*
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.HashMap
import java.util.concurrent.TimeUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var naverMap: NaverMap

    private lateinit var mapView: MapView
    var latlngList = mutableListOf<LatLng>()
    var pointList = mutableListOf<LatLng>()
    var list_RoadviewInfo = arrayListOf<RoadviewInfo>()
    var list_DatabaseInfo = arrayListOf<DatabaseInfo>()
    val circle = CircleOverlay()

    var ROADVIEWINFO_INDEX = 0
    val DISTANCE_BETWEEN_LOCATION_OBSTACLE = 3
    val DISTANCE_BETWEEN_LOCATION_PLACE = 7
    var ttsStart = false
    var ttsDest = false
    //val DISTANCE_BETWEEN_LOCATION_POINT = 15

    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.MINUTES)
        .build();
    val BASE_URL_FLAT_API ="http://13.125.253.43:8080/" //"http://15.164.166.74:8080"(민영) //"http://10.0.2.2:3000"(에뮬레이터-로컬서버 통신)
    val gson = GsonBuilder().setLenient().create()

    private val sharedViewModel:SharedViewModel by activityViewModels()
    private lateinit var cameraLatLng: LatLng
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    //위치
    private lateinit var locationSource: FusedLocationSource
    //TTS 클라이언트
    var ttsClient : TextToSpeechClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val options = NaverMapOptions()
            .camera(CameraPosition(LatLng(35.1798159, 129.0750222), 8.0))
            .mapType(NaverMap.MapType.Terrain)
        //val mapFragment = MapView.newInstance(options)
        mapView = view.findViewById(R.id.navermap) as MapView

        sharedViewModel.infoLiveData.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(),"sharedViewModel", Toast.LENGTH_LONG).show()
            parsingInfo(it)
        })

        mapView.getMapAsync(this)
        Log.i("mapView","getMapAsync시작")
        textToSpeech()
        //getuserlocationMapFragment()
        //locationUpdates()
        //locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
//        button_map_voice.setOnClickListener{view->
//            var permission_audio = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
//            var permission_storage = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            var permission_network = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_NETWORK_STATE)
//            var permission_internet = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET)
//
//            if(permission_audio != PackageManager.PERMISSION_GRANTED &&
//                permission_storage != PackageManager.PERMISSION_GRANTED
//            ) {
//                Log.d("Kakao", "Permission to recode denied")
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 0)
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
//            } else {
//                //음성인식과 음성합성 초기화
//                SpeechRecognizerManager.getInstance().initializeLibrary(requireContext())
//                TextToSpeechManager.getInstance().initializeLibrary(requireContext())
//                editText_inputway_start.text.clear()
//                editText_inputway_end.text.clear()
//                textView_inputway_speech.setText("")
//                textView_inputway_speech.setVisibility(View.VISIBLE)
//
//                textToSpeech()
//            }
//
//        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }else{
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(p0: NaverMap) {

        naverMap = p0
        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true
        Log.i("onMapReady","onMapReady안")

        Log.d("locationOverlay",locationOverlay.position.toString())
        naverMap.locationSource = locationSource //현재 위치

        Log.d("changeLocation in onMapReady",(activity as MainActivity).loc.toString())

        //카메라 좌표
        //현재 위치
        if (!(activity as MainActivity).loc.equals(LatLng(0.0,0.0))) {
            //Log.d("현재 위치", "현재 위치")
            cameraLatLng = LatLng((activity as MainActivity).loc.latitude, (activity as MainActivity).loc.longitude)
            //위치 오버레이
            locationOverlay.position = LatLng((activity as MainActivity).loc.latitude, (activity as MainActivity).loc.longitude)
            locationOverlay.bearing = 90f
        }
        //출발지 위치
        val polyline = PolylineOverlay()
        if (latlngList.size != 0) {
            cameraLatLng = LatLng(latlngList[0].latitude, latlngList[0].longitude)
            polyline.setCoords(latlngList)
            polyline.setColor(Color.parseColor("#FF0000"))
            polyline.setWidth(7)

            Log.d("연결선",polyline.width.toString())
            polyline.setMap(naverMap)
        }

        if (this::cameraLatLng.isInitialized) {
            naverMap.cameraPosition = CameraPosition(cameraLatLng, 14.5)
        }
        naverMap.setMapType(NaverMap.MapType.Basic)

        naverMap.addOnLocationChangeListener { location ->
//            Toast.makeText(requireContext(), "${location.latitude}, ${location.longitude}",
//                Toast.LENGTH_SHORT).show()
            Log.d("naverMap.addOnLocationChangeListener","${location.latitude}, ${location.longitude}")
            if(naverMap.locationTrackingMode != LocationTrackingMode.None){
                circle.center = LatLng(location.latitude, location.longitude)
                circle.radius = 3.0
                circle.map = naverMap
                circle.color = Color.parseColor("#80FB7F78")
                var next_distance:Double
                var start_distance:Double
                var point_distance:Double
                var destination_distance:Double
                if(list_RoadviewInfo.isNotEmpty()) {
                    next_distance = distance(
                        list_RoadviewInfo[ROADVIEWINFO_INDEX].location.latitude,
                        list_RoadviewInfo[ROADVIEWINFO_INDEX].location.longitude,
                        location.latitude,
                        location.longitude,
                        "meter"
                    )
                    Log.d("next_distance", next_distance.toString())
                    if (next_distance <= DISTANCE_BETWEEN_LOCATION_OBSTACLE.toDouble()) {
                        //Log.d("distance","반경 10m 이내에 위험요소가 있습니다. 위험요소와의 거리는"+next_risk.toString()+"입니다")
                        ttsClient?.setSpeechText(
                            "위험요소와 가까이 있습니다. 위험요소와의 직선 거리는" + String.format(
                                "%.2f",
                                next_distance
                            ) + "미터 입니다."
                        )
                        ttsClient?.play()
                        if (ROADVIEWINFO_INDEX < list_RoadviewInfo.size - 1) {
                            ROADVIEWINFO_INDEX = ROADVIEWINFO_INDEX + 1
                        } else {
                            ROADVIEWINFO_INDEX = 0
                        }
                    }
                }
                if (latlngList.size != 0){
                    if (!ttsStart) {
                        start_distance = distance(latlngList[0].latitude, latlngList[0].longitude, location.latitude, location.longitude, "meter")
                        Log.d("start_distance",start_distance.toString())
                        if (start_distance <= DISTANCE_BETWEEN_LOCATION_PLACE.toDouble()){
                            Log.d("latlngList 안 안","***********")
                            //ttsClient?.stop()
                            ttsClient?.setSpeechText("현재 위치는 출발지입니다")
                            ttsClient?.play()
                            ttsStart = true
                        }
                    }
                    if (!ttsDest){
                        destination_distance = distance(latlngList[latlngList.size-1].latitude,latlngList[latlngList.size-1].longitude, location.latitude,location.longitude,"meter")
                        if(destination_distance <= DISTANCE_BETWEEN_LOCATION_PLACE.toDouble()){
                            //ttsClient?.stop()
                            ttsClient?.setSpeechText("최종 목적지에 도착했습니다")
                            ttsClient?.play()
                            ttsDest = true
                        }
                    }
                }
                if (pointList.size != 0){
//                    point_distance = distance(pointList[0].latitude, pointList[0].longitude, location.latitude, location.longitude,"meter")
//                    Log.d("point_distance",point_distance.toString())
//                    Log.d("point_distance_point",pointList[0].latitude.toString())
//                    if (point_distance <= DISTANCE_BETWEEN_LOCATION_PLACE.toDouble()){
//                        ttsClient?.setSpeechText("다음 목적지까지 거리는 "+String.format("%.2f", point_distance) +"미터 입니다.")
//                        ttsClient?.play()
//                        pointList.removeAt(0)
//                    }
                }
            }
        }

        ////////////////////////////////////////
        // RoadviewInfo 마커 띄우기 및 클릭이벤트 //
        if(list_RoadviewInfo.isNotEmpty()){
            //ROADVIEWINFO_INDEX = 0
            for(i in 0 until list_RoadviewInfo.size){
                val marker = Marker()
                marker.width = 105
                marker.height = 105
                marker.icon = OverlayImage.fromResource(R.drawable.danger)

                Log.d("마커",marker.width.toString())
                Log.d("마커",marker.height.toString())
                //marker.icon = OverlayImage.fromResource(R.drawable.danger)
                marker.position = list_RoadviewInfo[i].location
                marker.map = naverMap
                marker.setOnClickListener {
                    // RoadviewInfo 객체를 액티비티로 전달
                    val intent = Intent(activity,MarkerResultActivity::class.java)
                    val args = Bundle()
                    args.putParcelable("location",list_RoadviewInfo[i].location)
                    intent.putExtra("bundle", args)

                    val decodedBytes = Base64.decode(list_RoadviewInfo[i].image,0)
                    //val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    intent.putExtra("image",decodedBytes)
                    startActivity(intent)
                    true
                }
            }
        }

        /////////////////////////////////////////
        // DatabaseInfo 마커 띄우기 및 클릭이벤트 //
        if(list_DatabaseInfo.isNotEmpty()){
            for(i in 0 until list_DatabaseInfo.size){
                val marker2 = Marker()
                marker2.width = 76
                marker2.height = 110
                marker2.position = list_DatabaseInfo[i].latlng
                marker2.map = naverMap
                var obstacle =""
                if(list_DatabaseInfo[i].OBSTACLE_ID=="1"){
                    obstacle = "결빙구간"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="2"){
                    obstacle = "공사중"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="3"){
                    obstacle = "보도장애시설"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="4"){
                    obstacle = "급경사"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="5"){
                    obstacle = "횡단보도"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="6"){
                    obstacle = "보도 턱"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="7"){
                    obstacle = "횡단보도 턱"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="8"){
                    obstacle = "진입제어봉"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="9"){
                    obstacle = "음향신호기"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="10"){
                    obstacle = "육교"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="11"){
                    obstacle = "교량"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="12"){
                    obstacle = "고가도로"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="13"){
                    obstacle = "계단"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="14"){
                    obstacle = "승강기"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="15"){
                    obstacle = "에스컬레이터"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="16"){
                    obstacle = "휠체어리프트"
                }else if(list_DatabaseInfo[i].OBSTACLE_ID=="17"){
                    obstacle = "경사로"
                }
                marker2.setOnClickListener {
                    // DatabaseInfo 객체를 액티비티로 전달
                    val intent = Intent(activity,DatabaseResultActivity::class.java)
                    val args = Bundle()
                    args.putParcelable("location",list_DatabaseInfo[i].latlng)
                    intent.putExtra("bundle", args)
                    intent.putExtra("obstacle",obstacle)

                    if(list_DatabaseInfo[i].IMGNAME.isNotEmpty()){
                        val decodedBytes = Base64.decode(list_DatabaseInfo[i].IMGNAME,0)
                        //val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        intent.putExtra("image",decodedBytes)
                    }
                    if(list_DatabaseInfo[i].FEATURE.isNotEmpty()){
                        intent.putExtra("feature",list_DatabaseInfo[i].FEATURE)
                    }
                    startActivity(intent)
                    true
                }
            }
        }

        //val obstacle = findViewById<Button>(R.id.obstacle)

        obstacle.setOnClickListener {
            // 마커추가
            val marker = Marker()
            marker.position = LatLng(37.5670135, 126.9783740)
            marker.map = naverMap

            // 현재 지도 화면의 좌표 출력
            val cameraPosition = naverMap.cameraPosition
            //Toast.makeText(this,cameraPosition.toString(), Toast.LENGTH_SHORT).show()
            Log.i("현재cameraPosition좌표", cameraPosition.toString())

            val projection = naverMap.projection

            val point_first_marker = projection.toScreenLocation(LatLng(37.57037156583255, 126.98314335390604))
            Log.i("첫번째 마커 화면좌표", point_first_marker.toString())

            val point = projection.toScreenLocation(LatLng(cameraPosition.target.latitude,cameraPosition.target.longitude))
            Log.i("현재cameraPosition 화면좌표", point.toString())

            val coord = projection.fromScreenLocation(PointF(0f, 0f)) // 화면의 (0,0) 지점을 지도 좌표로 변환 -> 왼쪽 끝이 (0,0)
            Log.i("화면좌표(0,0)", coord.toString())

            val height = naverMap.height.toFloat()
            val width = naverMap.width.toFloat()

            Log.i("화면좌표(오른쪽 아래 모서리)", projection.fromScreenLocation(PointF(width, height)).toString()) // 오른쪽 끝이 (1080,1920) = 현재 에뮬레이터의 크기 대로
            Log.i("화면좌표(오른쪽 위 모서리)",projection.fromScreenLocation(PointF(width, 0f)).toString())
            Log.i("화면좌표(왼쪽 아래 모서리)",projection.fromScreenLocation(PointF(0f,height)).toString())

            sendToServerMapCoord(projection.fromScreenLocation(PointF(width, 0f)),
                        projection.fromScreenLocation(PointF(0f, 0f)),
                        projection.fromScreenLocation(PointF(0f,height)),
                        projection.fromScreenLocation(PointF(width, height)))
        }
        //위치 추적 모드
        naverMap.locationTrackingMode = LocationTrackingMode.None
    }
    private fun distance(lat1:Double, lon1:Double, lat2:Double, lon2:Double, unit:String):Double {

        val theta = lon1 - lon2;
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private fun deg2rad(deg:Double):Double {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private fun rad2deg(rad:Double):Double {
        return (rad * 180 / Math.PI);
    }


    private fun textToSpeech(){
        SpeechRecognizerManager.getInstance().initializeLibrary(requireContext())
        TextToSpeechManager.getInstance().initializeLibrary(requireContext())
        //TTS 클라이언트 생성
        ttsClient = TextToSpeechClient.Builder()
            .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     // 음성합성방식
            .setSpeechSpeed(0.9)                                  // 발음 속도
            .setSpeechVoice(TextToSpeechClient.VOICE_MAN_READ_CALM)  //TTS 음색 모드 설정(여성 차분한 낭독체)
            .setListener(object : TextToSpeechListener {
                //음성합성이 종료될 때 호출된다.
                override fun onFinished() {
                    val intSentSize = ttsClient?.getSentDataSize()      //세션 중에 전송한 데이터 사이즈
                    val intRecvSize = ttsClient?.getReceivedDataSize()  //세션 중에 전송받은 데이터 사이즈
                    val strInacctiveText = "handleFinished() SentSize : $intSentSize  RecvSize : $intRecvSize"
                    ttsClient?.stop()
//                    handler.postDelayed(Runnable {
//                        sttclient.startRecording(true)
//                    }, 0)
                    Log.i("kakao", strInacctiveText)
                }
                override fun onError(code: Int, message: String?) {
                    Log.d("kakao", code.toString())
                }
            })
            .build()

    }

    private fun sendToServerMapCoord(oneLatLng:LatLng, twoLatLng:LatLng, threeLatLng:LatLng, fourLatLng:LatLng){
        var coordOne = oneLatLng.latitude.toString()+","+oneLatLng.longitude.toString()
        var coordTwo = twoLatLng.latitude.toString()+","+twoLatLng.longitude.toString()
        var coordThree = threeLatLng.latitude.toString()+","+threeLatLng.longitude.toString()
        var coordFour = fourLatLng.latitude.toString()+","+fourLatLng.longitude.toString()
        val api2 = Retrofit.Builder()
            .baseUrl(BASE_URL_FLAT_API).client(okHttpClient) //"http://192.168.219.107:8080/" http://10.0.2.2:8080 http://192.168.219.107:8080/
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val client = api2.create(FlatAPI::class.java)
        client.postMapCoord(coordOne,coordTwo,coordThree,coordFour).enqueue(object : Callback <Array<JsonObject>> {
            override fun onResponse(call: Call <Array<JsonObject>> , response: Response <Array<JsonObject>> ) {
                if (response?.isSuccessful){
                    Toast.makeText(requireContext(), "지도 좌표 전송 성공", Toast.LENGTH_SHORT).show()
                    if ( response != null ){
                        var result = response.body()
                        var riskInfo = result?.get(0)

                        //Log.d("출력",riskInfo.toString())
//                        var result = response.body()
//                        if (result != null){
//                            //경로가 비어있는 경우
//                            Log.d("성공 onResponse 안","")
//                        }
                        if (result != null){
                            for (i in 0 until result.size){
                                val info = result[i].get("info").toString()
                                        //Log.d("info",info)
                                        // info = 'INFO_ID/OBSTACLEID/LONGITUDE/LATITUDE/FEATURE/IMGNAME'
                                val splitArray = info.split("/")
                                val info_id = splitArray[0]
                                val obstacle_id = splitArray[1]
                                val latlng = LatLng(splitArray[3].toDouble(), splitArray[2].toDouble())
                                val feature = splitArray[4]
                                val imgname = splitArray[5]
                                val databaseInfo = DatabaseInfo(info_id,obstacle_id,latlng,feature,imgname) //최종 객체
                                list_DatabaseInfo.add(databaseInfo)
                            }

                                //Log.d("출력",result?.get(i).toString())
                                for(i in 0 until list_DatabaseInfo.size){
                                    val marker2 = Marker()
                                    marker2.width = 76
                                    marker2.height = 110
                                    marker2.position = list_DatabaseInfo[i].latlng
                                    marker2.map = naverMap
                                    var obstacle =""
                                    if(list_DatabaseInfo[i].OBSTACLE_ID=="1"){
                                        obstacle = "결빙구간"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="2"){
                                        obstacle = "공사중"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="3"){
                                        obstacle = "보도장애시설"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="4"){
                                        obstacle = "급경사"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="5"){
                                        obstacle = "횡단보도"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="6"){
                                        obstacle = "보도 턱"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="7"){
                                        obstacle = "횡단보도 턱"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="8"){
                                        obstacle = "진입제어봉"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="9"){
                                        obstacle = "음향신호기"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="10"){
                                        obstacle = "육교"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="11"){
                                        obstacle = "교량"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="12"){
                                        obstacle = "고가도로"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="13"){
                                        obstacle = "계단"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="14"){
                                        obstacle = "승강기"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="15"){
                                        obstacle = "에스컬레이터"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="16"){
                                        obstacle = "휠체어리프트"
                                    }else if(list_DatabaseInfo[i].OBSTACLE_ID=="17"){
                                        obstacle = "경사로"
                                    }
                                    marker2.setOnClickListener {
                                        // DatabaseInfo 객체를 액티비티로 전달
                                        val intent = Intent(activity,DatabaseResultActivity::class.java)
                                        val args = Bundle()
                                        args.putParcelable("location",list_DatabaseInfo[i].latlng)
                                        intent.putExtra("bundle", args)
                                        intent.putExtra("obstacle",obstacle)

                                        if(list_DatabaseInfo[i].IMGNAME.isNotEmpty()){
                                            val decodedBytes = Base64.decode(list_DatabaseInfo[i].IMGNAME,0)
                                            //val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                            intent.putExtra("image",decodedBytes)
                                        }
                                        if(list_DatabaseInfo[i].FEATURE.isNotEmpty()){
                                            intent.putExtra("feature",list_DatabaseInfo[i].FEATURE)
                                        }
                                        startActivity(intent)
                                        true
                                    }
                                }
                                //val jsonObject2 = jsonArray.getJSONObject(i)
//                                var x = jsonObject2.getString("category").split('>')
//                                var y = if (x.size > 1) x.get(1) else x.get(0)
//                                itemList.add(ItemList(jsonObject2.getString("title").replace(re,""), y,
//                                    jsonObject2.getString("address"),
//                                    jsonObject2.getString("roadAddress"),
//                                    jsonObject2.getString("mapx"),
//                                    jsonObject2.getString("mapy"))
//                                )

                        }
                    }
                }else{
                    Log.d("실패 onResponse 안","")
                    //Toast.makeText(requireContext(), "onResponse ", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Array<JsonObject>>, t: Throwable) {
                Log.d("실패 onFailure 안", t.message)
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                //finish()
            }
        })
    }
    //서버에서 받은 데이터 파싱
    private fun parsingInfo(result:Array<Array<JsonObject>>){
        var serverRouteInfo = result?.get(0)
        var serverRoadviewInfo = result?.get(1)
        var serverDatabaseInfo = result?.get(2)

        if (serverRouteInfo != null) {
            parseRouteJson(serverRouteInfo)
        }

        if (serverRoadviewInfo != null) {
            for(i in 0 until serverRoadviewInfo.size){
                val jsonObject = serverRoadviewInfo[i]
                //val location = jsonObject.get("location").asString
                val location = jsonObject.get("location").toString()
                Log.d("location 확인",location)
                // [ longtitude 경도 127.xx, latitude 위도 37.xx ] 파싱
                val tmp = location.split("[")[1]
                val longtitude = tmp.split(",")[0].toDouble()
                val latitude = tmp.split(",")[1].split("]")[0].toDouble()
                val latlng = LatLng(latitude,longtitude)
                val image = jsonObject.get("image").asString
                val roadviewInfo = RoadviewInfo(latlng,image)
                list_RoadviewInfo.add(roadviewInfo)
            }
        }
        Log.d("list_RoadviewInfo 확인",list_RoadviewInfo.toString())

        val location = list_RoadviewInfo.get(0).location
        val imgstr = list_RoadviewInfo.get(0).image
        Log.d("location",location.toString())
        Log.d("imgStr",imgstr.toString())

        //var list_DatabaseInfo = arrayListOf<DatabaseInfo>()
        if (serverDatabaseInfo != null) {
            for(i in 0 until serverDatabaseInfo.size){
                val info = serverDatabaseInfo[i].get("info").toString()
                //Log.d("info",info)
                // info = 'INFO_ID/OBSTACLEID/LONGITUDE/LATITUDE/FEATURE/IMGNAME'
                val splitArray = info.split("/")
                val info_id = splitArray[0]
                val obstacle_id = splitArray[1]
                val latlng = LatLng(splitArray[3].toDouble(), splitArray[2].toDouble())
                val feature = splitArray[4]
                val imgname = splitArray[5]
                val databaseInfo = DatabaseInfo(info_id,obstacle_id,latlng,feature,imgname) //최종 객체
                list_DatabaseInfo.add(databaseInfo)
            }
        }
        Log.d("list_DatabaseInfo 확인",list_DatabaseInfo.toString())

    }

    //경로json 파싱
    private fun parseRouteJson(route: Array<JsonObject>){
        Log.d("parseRouteJson","파싱 시작")
        for (i in 0..route.size-1){
            val rObject = route.get(i)
            val geo = rObject.getAsJsonObject("geometry")
            //Log.d("geometry",geo.toString())
            //Log.d("type",geo.get("type").toString())
            if (geo.get("type").toString().contains("LineString")){
                val arrCoords = geo.getAsJsonArray("coordinates")
                for (j in 0..arrCoords.size()-1){
                    //Log.d("arrCoords", arrCoords[j].toString())
                    latlngList.add(LatLng(arrCoords[j].asJsonArray[1].asDouble,arrCoords[j].asJsonArray[0].asDouble))
                }
            } else if (geo.get("type").toString().contains("Point")){
                val arrCoords = geo.getAsJsonArray("coordinates")
                Log.d("pointList",arrCoords[0].toString()+","+arrCoords[1].toString())
                //Point 좌표만
                //pointList.add(LatLng(arrCoords[1].asDouble,arrCoords[0].asDouble))
            }
        }
        var start = latlngList[0]
        var destination = latlngList[latlngList.size-1]
        cameraLatLng = latlngList[0]
        Log.d("start",start.toString())
        Log.d("destination",destination.toString())
        Log.d("latlngList 확인",latlngList.toString())
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }
    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView!!.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}