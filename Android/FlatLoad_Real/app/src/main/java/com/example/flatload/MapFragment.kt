package com.example.flatload

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.fragment_input_way.*
import kotlinx.android.synthetic.main.fragment_map.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.HashMap

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
    var list_RoadviewInfo = arrayListOf<RoadviewInfo>()
    var list_DatabaseInfo = arrayListOf<DatabaseInfo>()

    private val sharedViewModel:SharedViewModel by activityViewModels()
    private lateinit var cameraLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        mapView = view.findViewById(R.id.navermap) as MapView
        sharedViewModel.infoLiveData.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(),"sharedViewModel", Toast.LENGTH_LONG).show()
            parsingInfo(it)
        })
        mapView.getMapAsync(this)
        Log.i("mapView","getMapAsync시작")
    }

    override fun onMapReady(p0: NaverMap) {
        Log.i("onMapReady","onMapReady안")
        naverMap = p0

        //카메라 좌표
        //현재 위치
        if (!(activity as MainActivity).loc.equals(LatLng(0.0,0.0))) {
            //Log.d("현재 위치", "현재 위치")
            cameraLatLng = LatLng((activity as MainActivity).loc.latitude, (activity as MainActivity).loc.longitude)
        }
        //출발지 위치
        val polyline = PolylineOverlay()
        if (latlngList.size != 0) {
            cameraLatLng = LatLng(latlngList[0].latitude, latlngList[0].longitude)
            polyline.setCoords(latlngList)
            polyline.setColor(Color.parseColor("#FF0000"))
            polyline.setWidth(7)
            //Log.d("연결선",polyline.width.toString())
            polyline.setMap(naverMap)
        }

        if (this::cameraLatLng.isInitialized)
            naverMap.cameraPosition = CameraPosition(cameraLatLng,14.5)
        naverMap.setMapType(NaverMap.MapType.Basic)
        ////////////////////////////////////////
        // RoadviewInfo 마커 띄우기 및 클릭이벤트 //
        if(list_RoadviewInfo.isNotEmpty()){
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

        }
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
            }
        }
        val start = latlngList[0]
        val destination = latlngList[latlngList.size-1]
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