package com.example.flatload

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import kotlinx.android.synthetic.main.fragment_map.*
import org.json.JSONArray


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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var naverMap: NaverMap
    //private val mapView: MapView? = null
    private lateinit var mapView: MapView
    var latlngList = mutableListOf<LatLng>()
    //private lateinit var viewModel : sharedViewModel
    //private val viewModel: sharedViewModel by activityViewModels()
//    private val viewModel: SharedViewModel by lazy {
//        ViewModelProvider(requireActivity(), object : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T =
//                SharedViewModel() as T
//        }).get(SharedViewModel::class.java)
//    }
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
//    override fun onActivityCreated(savedInstanceState: Bundle?){
//        super.onActivityCreated(savedInstanceState)
//        val mapView = view?.findViewById(R.id.navermap) as MapView
//        mapView.getMapAsync(this)
//    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.navermap) as MapView
        //mapView.onCreate(savedInstanceState)
//        sharedViewModel.routeLiveData.observe(this, Observer {
//            parseRouteJson(it)
//        })
//        sharedViewModel.roadInfoLiveData.observe(viewLifecycleOwner, Observer {
//            //로드뷰 정보
//            Log.d("로드뷰 정보",it.toString())
//        })
//        sharedViewModel.dbInfoLiveData.observe(viewLifecycleOwner, Observer {
//            //데이터베이스 정보
//            Log.d("데이터베이스 정보",it.toString())
//        })
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
            polyline.setMap(naverMap)
        }

        if (this::cameraLatLng.isInitialized)
            naverMap.cameraPosition = CameraPosition(cameraLatLng,14.0)
        naverMap.setMapType(NaverMap.MapType.Basic)
        // 마커 추가
        val marker2 = Marker()
        marker2.position = LatLng(37.560046407129505, 126.9753292489345)
        //marker2.position = LatLng(37.57037156583255, 126.98314335390604)
        marker2.map = naverMap

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
        // 나중에 메뉴 바의 높이 빼줘야함

        }
    }
    private fun parsingInfo(result:Array<Array<JsonObject>>){
        var serverRouteInfo = result?.get(0)
        var serverRoadviewInfo = result?.get(1)
        var serverDatabaseInfo = result?.get(2)
//        Log.d("serverRouteInfo",serverRouteInfo.get(0).toString())
//        Log.d("serverRoadviewInfo",serverRoadviewInfo.get(0).toString())
        if (serverRouteInfo != null) {
            parseRouteJson(serverRouteInfo)
        }
        var list_RoadviewInfo = arrayListOf<RoadviewInfo>()
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

        var list_DatabaseInfo = arrayListOf<DatabaseInfo>()
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
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