package com.example.flatload

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_add_risk.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import com.bumptech.glide.Glide
import com.google.gson.GsonBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private const val REQUEST_IMAGE_CAPTURE = 101    //카메라 사진 촬영 요청 코드
private const val REQUEST_PICK_IMAGE = 102       //앨범에서 가져오기 요청 코드

private const val MAX_IMAGE_SIZE = 1000000
/**
 * A simple [Fragment] subclass.
 * Use the [AddRiskFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddRiskFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val BASE_URL_FLAT_API = "http://3.36.64.66:8080"

    var permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)
    var compressQuality = 100     //압축률

    data class photo(val uri: Uri, val type: String, val location: String, val feature: String) //사용자 입력 데이터 클래스
    data class mediaImage(val id: Long, val displayName: String, val relativePath: String)     //MediaStore 데이터 클래스

    private lateinit var photoFile: File
    lateinit var providerUri: Uri    //앨범에서 가져오는 경우만 사용
    lateinit var photoUri: Uri
    lateinit var finalData : photo    //사용자 입력값
    lateinit var mediaimage : mediaImage  //MediaStore 칼럼 값
    lateinit var newFile : File
    lateinit var absolutepath : String
    lateinit var locFinal : LatLng
    //lateinit var metaDataLatLng : Pair<Float,Float>

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
        return inflater.inflate(R.layout.fragment_add_risk, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddRiskFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mgeocorder: Geocoder = Geocoder(requireContext(), Locale.getDefault())
        initCamera()
        //카메라 버튼 클릭
        button_addrisk_camera.setOnClickListener { view ->
            captureCamera()
        }
        //앨범 버튼 클릭
        button_addrisk_gallery.setOnClickListener { view ->
            getImage()
        }
        //현재 위치 버튼 클릭
        button_addrisk_now.setOnClickListener { view ->
            val txtLoc = mgeocorder.getFromLocation((activity as MainActivity).loc.latitude,(activity as MainActivity).loc.longitude,1)[0]
            locFinal = LatLng((activity as MainActivity).loc.latitude,(activity as MainActivity).loc.longitude)
            if(txtLoc.getAddressLine(0)!=null){
                editText_addrisk_location.setText(txtLoc.getAddressLine(0))
            }
        }
        //사진 데이터 버튼 클릭
        button_addrisk_data.setOnClickListener { view ->
            val mgeocorder = Geocoder(requireContext(), Locale.getDefault())
            //showexif 에서 계산한 실수는 float, latlng 은 double
            if(::locFinal.isInitialized){
                println("위도"+locFinal.latitude.toString()+"경도"+locFinal.longitude.toString())

                var txtLoc : List<Address> = mgeocorder.getFromLocation(locFinal.latitude,locFinal.longitude,1)
                if (txtLoc != null && txtLoc.size > 0){   //txtLoc가 null이 아니면서 txtLoc.size가 0인 경우가 있음....
                    var subtxtLoc = txtLoc.get(0)
                    if(subtxtLoc.getAddressLine(0)!=null){
                        editText_addrisk_location.setText(subtxtLoc.getAddressLine(0))
                        //locFinal = metaDataLatLng.first.toDouble()
                    }
                }else{
                    editText_addrisk_location.setText(locFinal.latitude.toString()+","+locFinal.longitude.toString())
                    Toast.makeText(requireContext(),"위치로 변환할 수 없는 좌표",Toast.LENGTH_SHORT).show()
                }
            //val txtLoc = mgeocorder.getFromLocation(metaDataLatLng.first.toDouble(),metaDataLatLng.second.toDouble(),1)[0]
            //if(txtLoc.getAddressLine(0)!=null){
            //    editText_addrisk_location.setText(txtLoc.getAddressLine(0))
            //}
            } else{
                Toast.makeText(requireContext(),"사진에 위치정보가 없음",Toast.LENGTH_SHORT).show()
            }
        }
        //등록 데이터 버튼 클릭
        button_addrisk_done.setOnClickListener { view ->
            try {
                val final_type = getType()
                val final_location =
                    locFinal.latitude.toString() + " " + locFinal.longitude.toString()
                val final_feature = editText_addrisk_comment.getText().toString()
                finalData = photo(photoUri, final_type, final_location,final_feature)
                //서버로 보내기
                println(finalData)
                if (::absolutepath.isInitialized) {        //초기화 되었을 때 //앨범에서 가져온 경우
                    newFile = createImageFile()
                    getFileResize(newFile, finalData.uri)
                    Log.d("MYTEST", newFile.length().toString())
                    testRetrofit(newFile)
                } else {                                  //초기화 안됐을 때 //
                    newFile = File(finalData.uri.path)
                    getFileResize(newFile, finalData.uri)
                    testRetrofit(newFile)
                }
            }catch (e: Exception){
                Toast.makeText(requireContext(),"위험요소 등록을 위한 정보가 부족합니다", Toast.LENGTH_SHORT).show()
            }
        }
        editText_addrisk_comment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                var input = editText_addrisk_comment.text.toString()
                textview_addrisk_countText.text = input.length.toString() + "/100"
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var input = editText_addrisk_comment.text.toString()
                textview_addrisk_countText.text = input.length.toString() + "/100"
            }
        })
    }

    private fun getType():String{
        return editText_addrisk_type.getText().toString()
    }
    fun setImageView(uri: Uri){
        Glide.with(requireActivity()).load(uri).into(imageView_addrisk_camera)
    }
    private fun getImage(){
        val intent = Intent(Intent.ACTION_PICK)  //ACTION_PICK 파일 선택하는 액티비티 띄우기
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }
    //임시 이미지 파일 생성
    private fun createImageFile(): File{
        val dir = requireActivity().getExternalCacheDir()   //외부 스토리지의 캐시 폴더 경로
        val file = File.createTempFile("photo_",".jpg", dir)  //File 클래스 임시 파일 생성 //photo_랜덤숫자.jpg
        return file
    }
//    var gson = GsonBuilder().setLenient().create()
//    val BASE_URL_FLAT_API = "http://112.148.189.103:8080"
//    val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL_FLAT_API)
//        .addConverterFactory(ScalarsConverterFactory.create())
//        .addConverterFactory(GsonConverterFactory.create(gson))
//        .build()
    private fun testRetrofit(file: File){
        var requestBodyType : RequestBody = RequestBody.create(MediaType.parse("text/plain"),finalData.type)
        var requestBodyLocation : RequestBody = RequestBody.create(MediaType.parse("text/plain"),finalData.location)
        var requestBodyFeature : RequestBody = RequestBody.create(MediaType.parse("text/plain"),finalData.feature)
        var map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
        map.put("location",requestBodyLocation)
        map.put("type",requestBodyType)
        map.put("feature",requestBodyFeature)

        var requestBody : RequestBody = RequestBody.create(MediaType.parse("image/*"),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("img",file.getName(),requestBody)
        var gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_FLAT_API) //"http://112.148.189.103:8080/"  192.168.219.107
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //viewModel.postImageData(body, map)
        val client = retrofit.create(FlatAPI::class.java)
        client.postImage(body, map).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response?.isSuccessful){
                    Toast.makeText(requireContext(), "위험요소가 등록되었습니다", Toast.LENGTH_SHORT).show()
                    Log.d("MYTEST","onResponse"+response?.body().toString())
                }else{
                    Toast.makeText(requireContext(), "위험요소 등록에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("MYTEST", t.message)
                Toast.makeText(requireContext(), "위험요소 등록에 실패했습니다", Toast.LENGTH_SHORT).show()
                //finish()
            }
        })
    }
    //카메라 앱 작동하고 캡처하기
    private fun captureCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)  //ACTION_IMAGE_CAPTURE를 이용해서 인텐트를 만들면 카메라 사용 가능
        if(intent.resolveActivity(requireActivity().getPackageManager()) != null){
            photoFile = createImageFile()
            //fileprovider 앱 내부 파일 공유하는데 사용
            if(Build.VERSION.SDK_INT<24){
                if(photoFile!=null){
                    val uri = Uri.fromFile(photoFile)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }else{
                if(photoFile != null) {
                    //startActivityForResult로 띄운 다른 액티비티에서 이 액티비티에 접근한 수 있도록 임시 권한이 부여된 파일의 uri
                    providerUri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.flatload.provider" ,   //$packageName.provider
                        photoFile
                    )  //임시 파일 uri 생성, manifest 파일에서 정의한 authorites
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, providerUri) //intent에 uri 전달
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE) //액티비티 띄움
                    Log.d("MYTEST","사진 촬영")
                }
            }
        }
    }

    //카메라에서 찍은 사진을 결과로 호출
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 카메라 촬영을 하면 이미지뷰에 사진 삽입
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            photoUri = Uri.fromFile(photoFile)      //실제 URI(file://으로 시작)
            setImageView(photoUri)
        }
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == AppCompatActivity.RESULT_OK) {
            if (data != null) {
                val uri = data.getData()                //선택한 이미지를 지칭하는 Uri 객체
                val resolver =
                    requireActivity().getContentResolver()     //Content provider로 해당 이미지에 대한 데이터를 SQLite 데이터베이스에서 읽어온다
                //Log.d("MYTEST", uri.toString())
                if (uri != null) {
                    val cursor = uri.let { resolver.query(it, null, null, null, null) }
                    cursor?.use {
                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                        val displayNameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        val relativePathColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                        while (cursor.moveToNext()) {
                            val id = cursor.getLong(idColumn)
                            val displayName = cursor.getString(displayNameColumn)
                            val relativePath = cursor.getString(relativePathColumn)
                            mediaimage = mediaImage(id, displayName, relativePath)
                            photoUri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            ) //mediastore URI
                            //viewModel.changeImageView(photoUri)            //CameraViewModel
                            setImageView(photoUri)
                            getAbsolutePath()
                            getMetaData(absolutepath)
                        }
                    }
                }
            }
        }
    }
    //사진 메타 데이터 불러오기
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAbsolutePath(){
        absolutepath = "/storage/emulated/0/"+ mediaimage.relativePath + mediaimage.displayName
        //getMetaData(absolutepath)
        Log.d("사진 절대 경로", absolutepath)
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getMetaData(absolutepath:String){
        var newAbsoluteFile = File(absolutepath)
        try {
            val exif = ExifInterface(newAbsoluteFile)         //ExifInterface로 사진 메타데이터 불러오기
            var x = showExif(exif)
            if (x != null){
                locFinal = LatLng(x.first.toDouble(), x.second.toDouble())
            }
        }catch (e : IOException){
            e.printStackTrace()
        }
    }
    private fun getTagString(tag: String, exif: ExifInterface): String? {
        return """$tag : ${exif.getAttribute(tag)}"""
    }
    //사진 메타데이터 불러오기
    private fun showExif(exif: ExifInterface): Pair<Float,Float>? {//LatLng? {
        var myAttribute: String? = "[Exif information] \n\n"
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        var latitude = getTagString(ExifInterface.TAG_GPS_LATITUDE, exif)
        var latitudeRef = getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif)
        var longitude = getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif)
        var longitudeRef = getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif)
        System.out.println(myAttribute);
        if (latitude != null && latitudeRef != null && longitude != null && longitudeRef != null) {
            if(latitude != "GPSLatitude : null" && latitudeRef != "GPSLatitudeRef : null"
                && longitude != "GPSLongitude : null" && longitudeRef !="GPSLongitudeRef : null") {
                val gpsArr = arrayOf(
                    latitude.split(':')[1].replace(" ", ""),
                    latitudeRef.split(':')[1].replace(" ", ""),
                    longitude.split(':')[1].replace(" ", ""),
                    longitudeRef.split(':')[1].replace(" ", "")
                )
                return getLatLng(gpsArr)
            }
            else{
                return null
            }
        }
        else{
            return null
        }
    }

    //좌표 계산 로직 1-1
    private fun getLatLng(gpsValue: Array<String>): Pair<Float,Float>{//LatLng{
        var latitude: Float
        if (gpsValue[1].equals("N")) {
            latitude = convertToDegree(gpsValue[0]);
        } else {
            latitude = 0 - convertToDegree(gpsValue[0]);
        }
        var longitude: Float
        if (gpsValue[3].equals("E")) {
            longitude = convertToDegree(gpsValue[2]);
        } else {
            longitude = 0 - convertToDegree(gpsValue[2]);
        }
        System.out.println("getLatLng")
        return Pair(latitude,longitude)
    }

    //좌표 계산 로직 1-2 gps 형태의 위치 정보를 좌표로 변환
    private fun convertToDegree(stringDMS: String): Float {
        var result: Float? = null   //Float
        val DMS = stringDMS.split(",")

        val stringD = DMS[0].split("/")
        val D0: Float= stringD[0].toFloat()
        val D1: Float = stringD[1].toFloat()
        val FloatD = D0 / D1

        val stringM = DMS[1].split("/")
        val M0: Float = stringM[0].toFloat()
        val M1: Float = stringM[1].toFloat()
        val FloatM = M0 / M1

        val stringS = DMS[2].split("/")
        val S0: Float = stringS[0].toFloat()
        val S1: Float = stringS[1].toFloat()
        val FloatS = S0 / S1

        result = (FloatD + FloatM / 60 + FloatS / 3600) //.toFloat()
        return result
    }
    //Uri를 비트맵으로 변환
    private fun getBitmapImage(photoUri: Uri) : Bitmap?{   //nullable 타입 정의
        var photoBitmap: Bitmap? = null
        try{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
                photoBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireActivity().getContentResolver(),photoUri))
                return photoBitmap
            }else{
                photoBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), photoUri)
                return photoBitmap
            }
        }catch(e: Exception){
            e.printStackTrace()
            Log.d("error","비트맵 변환 오류")
            return null
        }
    }
    //이미지 파일 압축
    private fun getFileResize(file: File, uri: Uri){
        var bitmap = getBitmapImage(uri)
        try{
            lateinit var fileoutputstream : FileOutputStream
            var streamLength = MAX_IMAGE_SIZE
            while(streamLength >= MAX_IMAGE_SIZE){
                fileoutputstream = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, fileoutputstream)  //fileoutputstream : bitmap 이미지를 저장
                Log.d("streamLength: ",streamLength.toString())
                streamLength = file.length().toInt()
                compressQuality -= 10                                                      //압축률
            }
            fileoutputstream.close()
        }catch (e: Exception){
            e.printStackTrace()
            Log.d("error","리사이징 오류")
        }
    }
    //카메라 권한 요청
    private fun initCamera(){
        var tmpPermission = checkPermision(arrayOf(permissions[0],permissions[1]))
        if (tmpPermission.isNotEmpty()){    //권한 허용 되어있지 않으면
            var tmpRequest = checkRequest(tmpPermission)
            if (tmpRequest.isNotEmpty())   //권한 요청을 이미 한 번 거절한 경우
            {
                var listRequest = tmpRequest.toArray(arrayOfNulls<String>(tmpRequest.size))
                println(listRequest)
                val snackBar = Snackbar.make(addriskLayout, "위험요소 추가를 위해 접근 권한이 필요합니다", Snackbar.LENGTH_INDEFINITE)

                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(requireActivity(),
                        listRequest,
                        200)
                }
                snackBar.show()
            }
            else {   //권한 요청을 처음 하는 경우
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(permissions[0],permissions[1]),
                    200
                )
            }
        }
        else{
            Log.i("카메라 권한 설정완료","")
        }
    }
    //권한에 대한 응답이 있을때 작동하는 메서드
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==200){ //허용받았으면
            if (grantResults.all{it == PackageManager.PERMISSION_GRANTED}){
            }
            else{
                Toast.makeText(requireContext(),"카메라 권한 제공에 동의하셔야 합니다", Toast.LENGTH_SHORT).show()
            }
        }
        if(requestCode==100){
            if (grantResults.all{it == PackageManager.PERMISSION_GRANTED}){
            }
            else{
                Toast.makeText(requireContext(),"위치 권한 제공에 동의하셔야 합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkPermision(permissionForCheck:Array<String>): ArrayList<String> {   //권한 허기 확인
        var rejectPermission = ArrayList<String>()
        for(permission in permissionForCheck){
            //ContextCompat가 안되서 ActivityCompat로 수정
            if(ActivityCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED){
                rejectPermission.add(permission)
            }
        }
        return rejectPermission
    }
    private fun checkRequest(tmpPermission: ArrayList<String>): ArrayList<String> { //한번 요청되었었는지 확인
        var showRequest = ArrayList<String>()
        for(permission in tmpPermission){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)){
                showRequest.add(permission)
            }
        }
        return showRequest
    }
}