package com.example.flatload

//import kotlinx.android.synthetic.main.activity_input_way.textviewJSONText

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.kakao.sdk.newtoneapi.*
import com.naver.maps.geometry.Tm128
import kotlinx.android.synthetic.main.fragment_input_way.*
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InputWayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputWayFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val sharedViewModel:SharedViewModel by activityViewModels()
    val okHttpClient = OkHttpClient.Builder()
        .readTimeout(15, TimeUnit.MINUTES)
        .build();
    val BASE_URL_FLAT_API ="http://13.125.252.44:8080/" //"http://15.164.166.74:8080"(??????) //"http://10.0.2.2:3000"(???????????????-???????????? ??????)
    val gson = GsonBuilder().setLenient().create()

    lateinit var origin: LatLng
    lateinit var destination: LatLng
    lateinit var routeOption: String

    var itemList = mutableListOf<ItemList>()
    //?????? ?????? ?????? ??????
    var routeSelected = false
    var answerYesNo = false
    //var YES_NO = false
    var ttsClient : TextToSpeechClient? = null

    var builder = SpeechRecognizerClient.Builder()
        .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_LOCAL)
    var sttclient = builder.build()
    val handler = Handler(Looper.getMainLooper())
    lateinit var mgeocorder: Geocoder

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
        var view = inflater.inflate(R.layout.fragment_input_way, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //????????? gps ??????
        //????????? ????????? ?????? ????????? ??????
        //?????? ?????? ????????? ?????????, ????????? -> ?????? ????????? ??????
        mgeocorder = Geocoder(requireContext(), Locale.getDefault())
        val items = resources.getStringArray(R.array.route_type)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        val inputMethodManager = getContext()?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        //initLocation()
        //??? ?????? ??????
        button_inputway_now.setOnClickListener { view ->
            //loc.latitude,loc.longitude <- ?????? ?????? ?????? ??????
            //?????? ??????-> ????????? ???????????? ????????? edittext??? ??????
            val txtLoc = mgeocorder.getFromLocation((activity as MainActivity).loc.latitude,(activity as MainActivity).loc.longitude,1)[0]
            origin = LatLng((activity as MainActivity).loc.latitude,(activity as MainActivity).loc.longitude)
            if(txtLoc.getAddressLine(0)!=null){
                editText_inputway_start.setText(txtLoc.getAddressLine(0))
            }
            //Log.i("my location", txtLoc.toString())
        }
        //?????? ??????
        button_inputway_yes.setOnSingleClickListener { view ->
            if(TextUtils.isEmpty(editText_inputway_start.text.toString()) || TextUtils.isEmpty(editText_inputway_end.text.toString()))
            {
                if(TextUtils.isEmpty(editText_inputway_start.text.toString()) && TextUtils.isEmpty(editText_inputway_end.text.toString())){
                    Toast.makeText(requireContext(),"???????????? ???????????? ??????????????????", Toast.LENGTH_LONG).show()
                }else{
                    if(TextUtils.isEmpty(editText_inputway_start.text.toString()))
                        Toast.makeText(requireContext(),"???????????? ??????????????????", Toast.LENGTH_LONG).show()
                    else if(TextUtils.isEmpty(editText_inputway_end.text.toString()))
                        Toast.makeText(requireContext(),"???????????? ??????????????????", Toast.LENGTH_LONG).show()
                }
            }else{
                Log.d("????????? ??????",editText_inputway_start.text.toString())
                Log.d("????????? ??????",editText_inputway_end.text.toString())
                if (::origin.isInitialized && ::destination.isInitialized) {
                    //listView.clearChoices()
                    listView.setVisibility(View.INVISIBLE)
                    sendToServerLatLng(origin, destination, routeOption)
                }else{
                    Toast.makeText(requireContext(),"????????? ???????????? ?????? ?????? ????????? ??????????????? ????????? ????????? ????????? ????????? ??????????????????", Toast.LENGTH_LONG).show()
                }
            }
        }
        // ?????? ??????
        button_inputway_no.setOnClickListener { view ->
            editText_inputway_start.text.clear()
            editText_inputway_end.text.clear()
            textView_inputway_speech.setText("")
            listView.setVisibility(View.INVISIBLE)
            spinner.setSelection(0)
        }
        //????????? ???????????? ??????
        imageButton1.setOnClickListener{view ->
            textView_inputway_speech.setVisibility(View.INVISIBLE)
            if (TextUtils.isEmpty(editText_inputway_start.getText().toString())) { ///////////////////
                Toast.makeText(requireContext(),"???????????? ?????? ????????????",Toast.LENGTH_SHORT).show()
            } else{
                getRocal(editText_inputway_start.getText().toString())
            }
            inputMethodManager.hideSoftInputFromWindow(imageButton1.windowToken, 0)
        }
        //????????? ???????????? ??????
        imageButton2.setOnClickListener{view->
            if (TextUtils.isEmpty(editText_inputway_end.getText().toString())) {
                Toast.makeText(requireContext(),"???????????? ?????? ????????????.",Toast.LENGTH_SHORT).show()
            }else{
                getRocal(editText_inputway_end.getText().toString())
            }
            inputMethodManager.hideSoftInputFromWindow(imageButton2.windowToken, 0)
        }
        //?????? ?????? ????????????
        listView.setOnItemClickListener { parent: AdapterView<*>, view: View, position: Int, id: Long ->
            var tm128 : Tm128
            val item = parent.getItemAtPosition(position) as ItemList
            if (editText_inputway_start.isFocused == true){
                editText_inputway_start.setText(if (item.roadAddress.length == 0) item.address else item.roadAddress)
                tm128 = Tm128(item.mapx.toDouble(),item.mapy.toDouble())
                origin = LatLng(tm128.toLatLng().latitude,tm128.toLatLng().longitude)
            }else if (editText_inputway_end.isFocused == true){
                editText_inputway_end.setText(if (item.roadAddress.length == 0) item.address else item.roadAddress)
                tm128 = Tm128(item.mapx.toDouble(),item.mapy.toDouble())
                destination = LatLng(tm128.toLatLng().latitude,tm128.toLatLng().longitude)
            }
            //listView.setVisibility(View.INVISIBLE)
        }
        //var permission_network = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_NETWORK_STATE)
        //var permission_internet = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET)

        button_inputway_voice.setOnClickListener { view->
            var permission_audio = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            var permission_storage = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission_audio != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 0)
            }
            if (permission_storage != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }
                //??????????????? ???????????? SDK ?????????
                SpeechRecognizerManager.getInstance().initializeLibrary(requireContext())
                TextToSpeechManager.getInstance().initializeLibrary(requireContext())

                editText_inputway_start.text.clear()
                editText_inputway_end.text.clear()
                //??????????????? ??????
                builder = SpeechRecognizerClient.Builder()
                    .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_LOCAL)
                sttclient = builder.build()

                textToSpeechClient()
                textToSpeech()

        }
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                when(position) {
                    0 -> {
                        routeOption = "0"   //??????(????????????)
                        routeSelected = false;
                    }
                    1 -> {
                        routeOption = "4"   //????????????
                        routeSelected = true;

                    }
                    2 -> {
                        routeOption = "10"   //????????????
                        routeSelected = true;
;
                    }
                    3 -> {
                        routeOption = "30"   //????????????
                        routeSelected = true;

                    }
                    else -> {
                        routeOption = "0"
                        routeSelected = true;

                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InputWayFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        SpeechRecognizerManager.getInstance().finalizeLibrary()
    }

    private fun textToSpeechClient(){
        ttsClient = TextToSpeechClient.Builder()
            .setSpeechMode(TextToSpeechClient.NEWTONE_TALK_1)     //??????????????????
            .setSpeechSpeed(0.9)                                  //????????????
            .setSpeechVoice(TextToSpeechClient.VOICE_MAN_READ_CALM)//TTS ???????????? ??????(?????? ????????? ?????????)
            .setListener(object : TextToSpeechListener {
                //??????????????? ????????? ??? ??????
                override fun onFinished() {
                    val intSentSize = ttsClient?.getSentDataSize()     //?????? ?????? ????????? ????????? ?????????
                    val intRecvSize = ttsClient?.getReceivedDataSize() //?????? ?????? ???????????? ????????? ?????????
                    val strInacctiveText = "handleFinished() SentSize : $intSentSize  RecvSize : $intRecvSize"
                    ttsClient?.stop()
                    speechToText()
                    Log.d("kakao",strInacctiveText)
                }
                override fun onError(code: Int, message: String?) {
                    Log.d("kakao", code.toString())
                }
            })
            .build()
    }
    private fun textToSpeech(){
        //?????????&?????????&????????????
        if (TextUtils.isEmpty(editText_inputway_start.getText().toString())) {
            Log.d("??????","?????????")
            ttsClient?.setSpeechText("???????????? ??????????????? ?????? ????????? ???????????? ????????? ?????? ???????????? ???????????????")
            ttsClient?.play()
        } else if(TextUtils.isEmpty(editText_inputway_end.getText().toString())){
            ttsClient?.setSpeechText("???????????? ???????????????")
            ttsClient?.play()
        } else {
            if (!routeSelected) {
                ttsClient?.setSpeechText("??????????????? ??????, ????????????, ????????????, ???????????? ????????? ?????? ??????????????????")
                ttsClient?.play()
            } else {
                ttsClient?.setSpeechText("????????? ????????? ????????? '??????', ????????? ????????? '??????'?????? ???????????????")
                ttsClient?.play()
            }
        }
    }
    private fun speechToText(){
        if (!TextUtils.isEmpty(editText_inputway_start.getText().toString())
            &&!TextUtils.isEmpty(editText_inputway_end.getText().toString())){
            if(!routeSelected) {
                builder = SpeechRecognizerClient.Builder()
                    .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WORD)
                    .setUserDictionary("??????\n????????????\n????????????\n????????????")
            }else if(!answerYesNo){
                builder = SpeechRecognizerClient.Builder()
                    .setServiceType(SpeechRecognizerClient.SERVICE_TYPE_WORD)
                    .setUserDictionary("???\n???\n?????????\n?????????")
            }
        }
        sttclient = builder.build()

        val handler = Handler(Looper.getMainLooper())
        sttclient.setSpeechRecognizeListener(object : SpeechRecognizeListener {
            override fun onReady() {
                handler.postDelayed(Runnable {
                    val toast = Toast.makeText(requireContext(), "???????????? ?????? ????????????", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL) //?????????????????? ??????
                    val group = toast.view as ViewGroup
                    val msgTextView = group.getChildAt(0) as TextView
                    msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f) //?????? ??????
                    toast.show()
                }, 0)
            }
            override fun onBeginningOfSpeech() {
                //Log.d("kakao", "????????? ??????")
            }
            override fun onEndOfSpeech() {
                handler.postDelayed(Runnable {
                    val toast = Toast.makeText(requireContext(), "?????????????????????", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL) //?????????????????? ??????
                    val group = toast.view as ViewGroup
                    val msgTextView = group.getChildAt(0) as TextView
                    msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20f) //?????? ??????
                    toast.show()
                }, 0)
                //Log.d("kakao", "????????? ??????. ????????? ??????")
            }
            override fun onPartialResult(partialResult: String?) {
                //Log.d("kakao", "????????? ?????????:" + partialResult)
                textView_inputway_speech.setText(partialResult)
            }
            override fun onResults(results: Bundle?) {
                val texts = results?.getStringArrayList(SpeechRecognizerClient.KEY_RECOGNITION_RESULTS)
                val confs = results?.getIntegerArrayList(SpeechRecognizerClient.KEY_CONFIDENCE_VALUES)
                //???????????? ?????? ????????? ???????????? ??????????????? ??????
                activity?.runOnUiThread {
                    textView_inputway_speech.setText(texts?.get(0))
                        if (texts?.get(0) == "???" || texts?.get(0) == "???" || texts?.get(0) == "???" ||
                            texts?.get(0) == "???" || texts?.get(0) == "??????"){
                            sttclient.cancelRecording()
                            textToSpeech()
                            answerYesNo = true
                        }else if(texts?.get(0) =="?????????" || texts?.get(0) == "?????????") {
                            sttclient.cancelRecording()

                            if (!(TextUtils.isEmpty(editText_inputway_start.getText().toString())) &&
                                 TextUtils.isEmpty(editText_inputway_end.getText().toString())) { // ????????? ??????
                                editText_inputway_start.text.clear()
                            } else if (!(TextUtils.isEmpty(editText_inputway_start.getText().toString())) &&
                                !(TextUtils.isEmpty(editText_inputway_end.getText().toString()))) {
                                if (routeSelected){ //?????? ?????? ??????
                                    spinner.setSelection(0)
                                }else{                   //????????? ??????
                                    editText_inputway_end.text.clear()
                                }
                            }
                            //?????????
                            ttsClient?.setSpeechText("?????? ???????????????")
                            ttsClient?.play()
                        } else if(texts?.get(0) =="??????"){
                        sttclient.cancelRecording()
                        sendToServerLatLng(origin, destination, routeOption)
                    }else if(texts?.get(0) =="??????") {
                        sttclient.cancelRecording()
                        editText_inputway_start.text.clear()
                        editText_inputway_end.text.clear()
                        spinner.setSelection(0)
                        textView_inputway_speech.setVisibility(View.INVISIBLE)
                    }else{
                        if (TextUtils.isEmpty(editText_inputway_start.getText().toString())|| TextUtils.isEmpty(editText_inputway_end.getText().toString())) {
                            if (texts?.get(0)=="?????? ??????"){
                                val txtLoc = mgeocorder.getFromLocation((activity as MainActivity).loc.latitude,(activity as MainActivity).loc.longitude,1)[0]
                                origin = LatLng((activity as MainActivity).loc.latitude,(activity as MainActivity).loc.longitude)
                                if(txtLoc.getAddressLine(0)!=null){
                                    editText_inputway_start.setText(txtLoc.getAddressLine(0))
                                }
                                textToSpeech()
                            }else {
                                texts?.get(0)?.let { getRocal(it) }
                            }
                            sttclient.cancelRecording()
                        }else{
                            when(texts?.get(0)){
                                "??????" -> {
                                    spinner.setSelection(0)
                                    routeOption = "0"   //??????(????????????)
                                    routeSelected = true
                                }
                                "????????????" -> {
                                    spinner.setSelection(1)
                                    routeOption = "4"   //????????????
                                    routeSelected = true
                                }
                                "????????????" -> {
                                    spinner.setSelection(2)
                                    routeOption = "10"   //????????????
                                    routeSelected = true
                                }
                                "????????????" -> {
                                    spinner.setSelection(3)
                                    routeOption = "30"   //????????????
                                    routeSelected = true
                                }
                                else -> {
                                    spinner.setSelection(0)
                                    routeOption = "0"
                                    routeSelected = true
                                }
                            }
                            answerYesNo = true
                            sttclient.cancelRecording()
                            ttsClient?.setSpeechText("?????? ????????? "+texts?.get(0)+" ?????????. ????????? ??? ????????? ??????????????? ???????????????.")
                            ttsClient?.play()
                        }
                    }
                }
            }
            override fun onAudioLevel(audioLevel: Float) {
                //Log.d(TAG, "Audio Level(0~1): " + audioLevel.toString())
            }
            override fun onError(errorCode: Int, errorMsg: String?) {
                Log.d("kakao", "Error: " + errorMsg)
            }
            override fun onFinished() {
                //Log.d("kakao", "Finished")
            }
        })
        handler.postDelayed(Runnable {
            sttclient.startRecording(true)
        }, 0)
    }
    // ????????? ?????? ??????
    private fun sendToServerLatLng(startLatLng: LatLng, destLatLng: LatLng, routeOption:String){
        //val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"'"+jsonStartDest.toString()+"'")
        // MediaType.parse("application/json; charset=utf-8");
        var startString = startLatLng.latitude.toString()+","+startLatLng.longitude.toString()
        var destString = destLatLng.latitude.toString()+","+destLatLng.longitude.toString()

        val api2 = Retrofit.Builder()
            .baseUrl(BASE_URL_FLAT_API).client(okHttpClient) //"http://192.168.219.107:8080/" http://10.0.2.2:8080 http://192.168.219.107:8080/
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val client = api2.create(FlatAPI::class.java)
        client.postPoint(startString,destString,routeOption).enqueue(object : Callback <Array<Array<JsonObject>>> {
        override fun onResponse(call: Call <Array<Array<JsonObject>>> , response: Response <Array<Array<JsonObject>>> ) {
            if (response?.isSuccessful){
                Toast.makeText(requireContext(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show()
                if ( response != null ){
                    var result = response.body()

                    var serverRouteInfo = result?.get(0)
                    Log.d("??????",serverRouteInfo.toString())
                    Log.d("??????", serverRouteInfo?.size.toString())

                    if (result != null){
                        //????????? ???????????? ??????
                        if (serverRouteInfo?.size != 0){ // && serverRouteInfo.isEmpty()) {
                            sharedViewModel.changeInfo(result)
                            //}
                        }else{
                            Toast.makeText(requireContext(),"?????? ???????????? 3km ????????? ?????? ????????? ????????? ?????? ?????????.", Toast.LENGTH_LONG).show()
                        }
                    }
                    //MapFragment??? ??????
                    (activity as MainActivity?)?.setFragment(MapFragment(), "1")
                }
            }else{
                Log.d("?????? onResponse ???","")
                //Toast.makeText(requireContext(), "onResponse ", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onFailure(call: Call<Array<Array<JsonObject>>>, t: Throwable) {
            Log.d("?????? onFailure ???", t.message)
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            //finish()
        }
    })
    }

    private fun getRocal(search:String){
        rocalSearchRetrofit(search)
    }
    //Naver rocal search
    private fun rocalSearchRetrofit(search:String){
        var map: HashMap<String, String> = HashMap<String, String>()
        map.put("query",search)
        map.put("display","5")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/v1/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        itemList.clear()
        val re = "<b>|</b>".toRegex()
        val client = retrofit.create(FlatAPI::class.java)
        client.getSearchRocal(getString(R.string.client_id_naver_rocal),
            getString(R.string.access_token_naver_rocal),
            map).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful){
                    val jsonObject = JSONObject(response.body()?.trimIndent())
                    val jsonArray = jsonObject.getJSONArray("items")
                    for (i in 0 until jsonArray.length()){
                        val jsonObject2 = jsonArray.getJSONObject(i)
                        var x = jsonObject2.getString("category").split('>')
                        var y = if (x.size > 1) x.get(1) else x.get(0)
                        itemList.add(ItemList(jsonObject2.getString("title").replace(re,""), y,
                            jsonObject2.getString("address"),
                            jsonObject2.getString("roadAddress"),
                            jsonObject2.getString("mapx"),
                            jsonObject2.getString("mapy"))
                        )
                    }
                    if(itemList.size<1) {
                        Toast.makeText(requireContext(), "?????? ????????? ???????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show()
                    }else{
                        //?????? textview??? ??????????????? ?????? ?????? ????????? ?????????
                        if (TextUtils.isEmpty(textView_inputway_speech.getText().toString())) {
                            textView_inputway_speech.setVisibility(View.GONE)  //???????????? ??? ?????????
                            //?????? ?????? ???????????? ?????????
                            listView.setVisibility(View.VISIBLE)
                            //???????????? ????????? ??????
                            listView.adapter = ListViewAdapter(itemList)
                        }else{
                            //?????? textview??? ???????????? ?????????
                            var tm128 = Tm128(itemList[0].mapx.toDouble(),itemList[0].mapy.toDouble())
                            if (TextUtils.isEmpty(editText_inputway_start.getText().toString())) {
                                editText_inputway_start.setText(itemList[0].roadAddress)
                                textView_inputway_speech.setText(textView_inputway_speech.text.toString()+
                                        "\n???????????? "+itemList[0].roadAddress+" ?????????.")
                                origin = LatLng(tm128.toLatLng().latitude,tm128.toLatLng().longitude)
                                ttsClient?.setSpeechText("???????????? "+itemList[0].roadAddress+" ?????????. ????????? ??? ????????? ??????????????? ???????????????.")
                                ttsClient?.play()
                                //YES_NO = true
                                //textToSpeech()
                            }else{
                                Log.d("?????????","????????????")
                                editText_inputway_end.setText(itemList[0].roadAddress)
                                textView_inputway_speech.setText(textView_inputway_speech.text.toString()+
                                        "\n???????????? "+itemList[0].roadAddress+" ?????????.")
                                destination = LatLng(tm128.toLatLng().latitude,tm128.toLatLng().longitude)
                                ttsClient?.setSpeechText("???????????? "+itemList[0].roadAddress+" ?????????. ????????? ??? ????????? ??????????????? ???????????????.")
                                ttsClient?.play()
                                //YES_NO = true
                            }
                        }
                    }
                }else{
                    Toast.makeText(requireContext(), "?????? ????????? ??????????????????", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("MYTEST", t.message)
                Toast.makeText(requireContext(), "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                //finish()
            }
        })
    }

    private fun checkDistance(distanceKm: Float):Int{
        if(distanceKm >= 3.00){
            Toast.makeText(requireContext(),"?????? ???????????? 3km ????????? ?????? ????????? ????????? ?????? ?????????.", Toast.LENGTH_LONG).show()
            editText_inputway_start.text.clear()
            editText_inputway_end.text.clear()
            return 0
        }
        return 1
    }

//    private fun startLocationUpdates() { //gps ??????
//        locationRequest = LocationRequest.create()?.apply {
//            interval= 10000
//            fastestInterval = 5000
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//        locationCallback = object : LocationCallback(){
//            override fun onLocationResult(locationResult: LocationResult?) {
//                //??????????????? ???????????? ???????????? ????????????? ??? ?????? ?????? ?????????
//                locationResult ?: return
//                for(location in locationResult.locations){
//                    loc= LatLng(location.latitude,location.longitude)
//                    Log.i("changeLocation",loc.toString())
//                }
//            }
//        }
//
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        fusedLocationClient?.requestLocationUpdates(
//            locationRequest,
//            locationCallback, //???????????? ????????? ??????
//            Looper.getMainLooper()) //?????????????????? ??????????????? ?????? ?????? ???????????????*/
//    }
//
//    private fun initLocation() {
//        if(ActivityCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//        )
//        {
//            getuserlocation() //???????????? ??????
//            startLocationUpdates() //????????????
//        }
//        else{
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),100)
//            //????????? ?????? ?????????
//        }
//    }
//
//    private fun getuserlocation() {
//        fusedLocationClient= LocationServices.getFusedLocationProviderClient(requireActivity())
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//        val temp = fusedLocationClient
//        if(temp != null){
//            temp.lastLocation
//                .addOnSuccessListener {//??????????????? ?????? ????????????????
//                    if (it == null) {
//                        Log.i("?????? ???????????? ??????", "")    //?????? ????????? ?????? ????????? ??? ?????? ??? ????????????
//                    } else {
//                        loc = LatLng(it.latitude, it.longitude)  //??????????????? ??????????????? ????????????
//                        Log.i("currentLocation", loc.toString())
//                    }
//                }
//                .addOnFailureListener{
//                    Log.i("location error","")          //
//                }
//        }
////        fusedLocationClient?.lastLocation?.addOnSuccessListener {//??????????????? ?????? ????????????????
////            loc = LatLng(it.latitude,it.longitude) //??????????????? ??????????????? ????????????
////            Log.i("currentLocation",loc.toString())
////        }
//    }

//    override fun onRequestPermissionsResult( //?????????????????? ?????? ????????? ???
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if(requestCode==100){ //??????????????????
//            if(grantResults[0]== PackageManager.PERMISSION_GRANTED &&
//                grantResults[1] == PackageManager.PERMISSION_GRANTED){ //?????? ????????????
//                getuserlocation()
//                startLocationUpdates()
//            }
//            else{//????????????????????? ?????? ?????????
//                Toast.makeText(requireContext(),"???????????? ????????? ????????? ?????????", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}