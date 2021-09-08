package com.example.flatload.view

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Geocoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.*
import java.io.File;
import java.lang.Exception
import java.util.*

import com.bumptech.glide.Glide

import com.example.flatload.base.BaseActivity
import com.example.flatload.databinding.ActivityMainBinding

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel

import kotlin.collections.HashMap
import com.example.flatload.viewmodel.CameraViewModel
import com.example.flatload.R
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_input_way.*

private const val REQUEST_IMAGE_CAPTURE = 101    //카메라 사진 촬영 요청 코드
private const val REQUEST_PICK_IMAGE = 102       //앨범에서 가져오기 요청 코드

private const val MAX_IMAGE_SIZE = 1000000

class CameraActivity : BaseActivity<ActivityMainBinding, CameraViewModel>(){
    override val layoutResourceId: Int
        get() = R.layout.activity_camera
    override val viewModel: CameraViewModel by viewModel("viewModelCameraPart")

    override fun initStartView() {
    }
    override fun initDataBinding() {
        //CamerViewModel의 LiveData 옵저빙
        viewModel.photolocationLiveData.observe(this, androidx.lifecycle.Observer{
            editText_photo_location.setText(it)
        })
        viewModel.typeLiveData.observe(this, androidx.lifecycle.Observer {
            editText_type.setText(it)
        })
        viewModel.imageviewLiveData.observe(this, androidx.lifecycle.Observer {
            setImageView(it)
        })
        viewModel.toastRegistrationLiveData.observe(this, androidx.lifecycle.Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        viewModel.metaDataLiveData.observe(this, androidx.lifecycle.Observer {
            locFinal = it
        })
    }
    override fun initAfterBinding() {
        init()
    }
    var compressQuality = 100     //압축률
    var permissions = arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION)

    data class photo(val uri: Uri, val type: String, val location: String)                      //사용자 입력 데이터 클래스
    data class mediaImage(val id: Long, val displayName: String, val relativePath: String)     //MediaStore 데이터 클래스

    private lateinit var photoFile: File
    lateinit var providerUri: Uri    //앨범에서 가져오는 경우만 사용
    lateinit var photoUri: Uri
    lateinit var finalData : photo    //사용자 입력값
    lateinit var mediaimage : mediaImage  //MediaStore 칼럼 값
    lateinit var newFile : File
    lateinit var absolutepath : String
    lateinit var locFinal : LatLng

    private fun testRetrofit(file: File){
        var requestBodyType : RequestBody = RequestBody.create(MediaType.parse("text/plain"),finalData.type)
        var requestBodyLocation : RequestBody = RequestBody.create(MediaType.parse("text/plain"),finalData.location)
        var map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
        map.put("location",requestBodyLocation)
        map.put("type",requestBodyType)

        var requestBody : RequestBody = RequestBody.create(MediaType.parse("image/*"),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("img",file.getName(),requestBody)

        viewModel.postImageData(body, map)
    }

    private fun init(){
        //권한 설정
        initCamera()

        //카메라 버튼 클릭
        button_camera.setOnClickListener{
            captureCamera()
        }
        //앨범 버튼 클릭
        button_gallery.setOnClickListener{
            getImage()
        }
        //현재 위치 버튼 클릭
        button_now_location.setOnClickListener{
            Log.d("MYTEST","현재 위치 버튼 클릭")
            locFinal = loc
            val mgeocorder = Geocoder(this, Locale.getDefault())
            val txtLoc = mgeocorder.getFromLocation(locFinal.latitude,locFinal.longitude,1)[0]
            if(txtLoc.getAddressLine(0)!=null){
                viewModel.changePhotoLocation(txtLoc.getAddressLine(0))
            }
        }
        //사진 데이터 버튼 클릭
        button_metadata_location.setOnClickListener {
            Log.d("MYTEST","사진 데이터 버튼 클릭")
            try{
                val mgeocorder = Geocoder(this, Locale.getDefault())
                val txtLoc = mgeocorder.getFromLocation(locFinal.latitude,locFinal.longitude,1)[0]
                if(txtLoc.getAddressLine(0)!=null){
                    viewModel.changePhotoLocation(txtLoc.getAddressLine(0))
                }
            }catch (e: Exception){
                Toast.makeText(this,"장애물 사진에 위치 데이터가 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
        //등록 버튼 클릭
        button_done.setOnClickListener{
            try {
                val final_type = getType()
                val final_location =
                    locFinal.latitude.toString() + " " + locFinal.longitude.toString()
                finalData = photo(photoUri, final_type, final_location)
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
                Toast.makeText(this,"위험요소 등록을 위한 정보가 부족합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getType():String{
        return editText_type.getText().toString()
    }
    fun setImageView(uri: Uri){
        Glide.with(this).load(uri).into(imageView4)
    }
    private fun getImage(){
        val intent = Intent(Intent.ACTION_PICK)  //ACTION_PICK 파일 선택하는 액티비티 띄우기
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
        startActivityForResult(intent,REQUEST_PICK_IMAGE)
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
                val snackBar = Snackbar.make(cameraLayout, "위험요소 추가를 위해 접근 권한이 필요합니다", Snackbar.LENGTH_INDEFINITE)

                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(this,
                        listRequest,
                        200)
                }
                snackBar.show()
            }
            else {   //권한 요청을 처음 하는 경우
                ActivityCompat.requestPermissions(
                    this,
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
                Toast.makeText(this,"카메라 권한 제공을 하셔야 합니다", Toast.LENGTH_SHORT).show()
            }
        }
        if(requestCode==100){
            if (grantResults.all{it == PackageManager.PERMISSION_GRANTED}){
            }
            else{
                Toast.makeText(this,"위치 권한 제공을 하셔야 합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //카메라 앱 작동하고 캡처하기
    private fun captureCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)  //ACTION_IMAGE_CAPTURE를 이용해서 인텐트를 만들면 카메라 사용 가능
        if(intent.resolveActivity(packageManager) != null){
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
                        this,
                        "$packageName.provider",   //$packageName.provider
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        // 카메라 촬영을 하면 이미지뷰에 사진 삽입
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            photoUri = Uri.fromFile(photoFile)      //실제 URI(file://으로 시작)
            viewModel.changeImageView(photoUri)     //CameraViewModel
            Log.d("MYTEST",photoUri.toString())
        }
        if(requestCode==REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.getData()                //선택한 이미지를 지칭하는 Uri 객체
                val resolver = getContentResolver()     //Content provider로 해당 이미지에 대한 데이터를 SQLite 데이터베이스에서 읽어온다
                Log.d("MYTEST",uri.toString())
                if (uri!=null) {
                    val cursor = uri.let { resolver.query(it, null, null, null, null) }
                    cursor?.use {
                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                        val displayNameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                        val relativePathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH)
                        while (cursor.moveToNext()) {
                            val id = cursor.getLong(idColumn)
                            val displayName = cursor.getString(displayNameColumn)
                            val relativePath = cursor.getString(relativePathColumn)
                            mediaimage = mediaImage(id, displayName, relativePath)
                            photoUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id) //mediastore URI
                            Log.d("MYTEST","*"+photoUri.toString())
                            viewModel.changeImageView(photoUri)            //CameraViewModel
                            getAbsolutePath()
                        }
                    }
                    System.out.println("출력");
                }
            }
        }
    }

    //임시 이미지 파일 생성
    private fun createImageFile(): File{
        val dir = externalCacheDir   //외부 스토리지의 캐시 폴더 경로
        val file = File.createTempFile("photo_",".jpg", dir)  //File 클래스 임시 파일 생성 //photo_랜덤숫자.jpg
        return file
    }
    //Uri를 비트맵으로 변환
    private fun getBitmapImage(photoUri: Uri) : Bitmap?{   //nullable 타입 정의
        var photoBitmap: Bitmap? = null
        try{
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                photoBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),photoUri))
                return photoBitmap
            }else{
                photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri)
                return photoBitmap
            }
        }catch(e:Exception){
            e.printStackTrace()
            System.out.println("비트맵 변환 오류")
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
                Log.d("MYTEST",streamLength.toString())
                streamLength = file.length().toInt()
                compressQuality -= 10                                                      //압축률
            }
            fileoutputstream.close()
        }catch (e:Exception){
            e.printStackTrace()
            Log.d("MYTEST","리사이징 오류")
        }
    }
    //사진 메타 데이터 불러오기(ViewModel)
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getAbsolutePath(){
        absolutepath = "/storage/emulated/0/"+ mediaimage.relativePath + mediaimage.displayName
        System.out.println("absolutePath: "+ absolutepath)
        viewModel.getMetaData(absolutepath)
    }
    private fun checkPermision(permissionForCheck:Array<String>): ArrayList<String>{   //권한 허기 확인
        var rejectPermission = ArrayList<String>()
        for(permission in permissionForCheck){
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                rejectPermission.add(permission)
            }
        }
        return rejectPermission
    }
    private fun checkRequest(tmpPermission: ArrayList<String>): ArrayList<String>{ //한번 요청되었었는지 확인
        var showRequest = ArrayList<String>()
        for(permission in tmpPermission){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
                showRequest.add(permission)
            }
        }
        return showRequest
    }

}
