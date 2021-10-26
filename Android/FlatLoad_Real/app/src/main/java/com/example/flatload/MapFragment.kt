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
//        val rootView = inflater.inflate(
//            R.layout.fragment_map,
//            container, false
//        ) as ViewGroup
//        val mapView = rootView.findViewById(R.id.navermap) as MapView
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this)
//        return rootView


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

        sharedViewModel.routeLiveData.observe(viewLifecycleOwner, Observer<String> {
            //데이터 받음
            String -> //Toast.makeText(requireContext(), String, Toast.LENGTH_SHORT).show()
            //Log.d("MapFragment 안",it)
            var routeJson = JSONArray(String)
            parseRouteJson(routeJson)
        })

        mapView.getMapAsync(this)
        Log.i("mapView","getMapAsync시작")

    }

    override fun onMapReady(p0: NaverMap) {
        Log.i("onMapReady","onMapReady안")
        naverMap = p0

        if (!(activity as MainActivity).loc.equals(LatLng(0.0,0.0))) {
            Log.d("현재 위치", "현재 위치")
            cameraLatLng = LatLng((activity as MainActivity).loc.latitude, (activity as MainActivity).loc.longitude)
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

        val polyline = PolylineOverlay()
        if (latlngList.size != 0) {
            polyline.setCoords(latlngList)
            polyline.setMap(naverMap)
        }
    }

    private fun parseRouteJson(route: JSONArray){
        Log.d("parseRouteJson안",route.getJSONObject(0).getJSONObject("properties").getString("totalDistance"))
        Log.d("parseRouteJson안",route.getJSONObject(0).getJSONObject("properties").getString("totalTime"))
        val distance = route.getJSONObject(0).getJSONObject("properties").getString("totalDistance").toFloat()/1000.0f
        val time = route.getJSONObject(0).getJSONObject("properties").getString("totalTime").toFloat()/60.0f

            //MapFragment
        for (i in 0 until route.length()){
            val json = route.getJSONObject(i)
            val jsonGeo = json.getJSONObject("geometry")
            if (jsonGeo.getString("type") == "LineString"){
                val arrCoords = jsonGeo.getJSONArray("coordinates") //jsonGeo["coordinates"] //jsonGeo.get("coordinates") //jsonGeo.getJSONArray("coordinates")
                //Log.d("좌표 확인2",arrCoords.toString())
                for (j in 0 until arrCoords.length()){
                    val arrLatLng = arrCoords[j].toString().substring(1,arrCoords[j].toString().length-1).split(",")
                    Log.d("test",arrLatLng.get(0)+" "+arrLatLng.get(1))
                    latlngList.add(LatLng(arrLatLng.get(1).toDouble(), arrLatLng.get(0).toDouble()))
                        //Log.d("test",latlngList.toString())
                        //Log.d("좌표 확인",arrayCoords[j].toString().split(",").get(1))
                }
                    //Log.d("coordinate 확인",jsonGeo.getJSONArray("coordinates").toString())
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