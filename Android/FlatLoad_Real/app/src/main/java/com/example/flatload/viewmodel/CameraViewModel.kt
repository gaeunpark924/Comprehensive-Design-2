package com.example.flatload.viewmodel

import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flatload.base.BaseViewModel
import com.example.flatload.model.DataModel
import com.google.android.gms.maps.model.LatLng
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

//사진에서 메타데이터 불러오는 로직
//CameraActivity에서 다른 로직들도 가져와야 함
class CameraViewModel(private val model: DataModel): BaseViewModel() {

    private val _typeLiveData = MutableLiveData<String>()
    private val _photolocationLiveData = MutableLiveData<String>()
    private val _imageviewLiveData = MutableLiveData<Uri>()
    private var _toastRegistrationLiveData = MutableLiveData<String>()
    private var _metaDataLiveData =  MutableLiveData<LatLng>()

    val photolocationLiveData: LiveData<String>   //view에서 observe하는 대상
        get() = _photolocationLiveData
    val typeLiveData: LiveData<String>
        get() = _typeLiveData
    val imageviewLiveData: LiveData<Uri>
        get() = _imageviewLiveData
    val toastRegistrationLiveData: LiveData<String>
        get() = _toastRegistrationLiveData
    val metaDataLiveData: LiveData<LatLng>
        get() = _metaDataLiveData

    fun changeType(type:String){
        _typeLiveData.postValue(type)             //LiveData의 값을 변경
    }
    fun changePhotoLocation(location:String){
        _photolocationLiveData.postValue(location)
    }
    fun changeImageView(uri: Uri){
        _imageviewLiveData.postValue(uri)
    }
    fun getThreadName():String { return Thread.currentThread().getName() }
    //disposable 추가
    fun postImageData(body: MultipartBody.Part, map:HashMap<String, RequestBody>){
        addDisposable(model.postData(body, map)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation()) // AndroidSchedulers.mainThread()
            .subscribe({
                it.run {
                    Log.d("TEST", "documents : $it")
                    _toastRegistrationLiveData.postValue("등록이 완료되었습니다")
                    Log.d("TEST",getThreadName() + " : result : " + it)
                }
            }, {
                Log.d("TEST", "response error, message : ${it.message}")
            })

        )

    }
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getMetaData(absolutepath:String){
        var newAbsoluteFile = File(absolutepath)

        try {
            val exif = ExifInterface(newAbsoluteFile)         //ExifInterface로 사진 메타데이터 불러오기
            var x = showExif(exif)
            if (x != null){
                _metaDataLiveData.postValue(x)
            }
        }catch (e : IOException){
            e.printStackTrace()
        }
    }
    private fun getTagString(tag: String, exif: ExifInterface): String? {
        return """$tag : ${exif.getAttribute(tag)}"""
    }
    //사진 메타데이터 불러오기
    private fun showExif(exif: ExifInterface): LatLng? {
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
                System.out.println("xxxxxxx")
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
    private fun getLatLng(gpsValue: Array<String>): LatLng {
        var latitude: Double
        if (gpsValue[1].equals("N")) {
            latitude = convertToDegree(gpsValue[0]);
        } else {
            latitude = 0 - convertToDegree(gpsValue[0]);
        }
        var longitude: Double
        if (gpsValue[3].equals("E")) {
            longitude = convertToDegree(gpsValue[2]);
        } else {
            longitude = 0 - convertToDegree(gpsValue[2]);
        }
        System.out.println("getLatLng")
        return LatLng(latitude, longitude)
    }

    //좌표 계산 로직 1-2 gps 형태의 위치 정보를 좌표로 변환
    private fun convertToDegree(stringDMS: String): Double {
        var result: Double? = null
        val DMS = stringDMS.split(",")

        val stringD = DMS[0].split("/")
        val D0: Double = stringD[0].toDouble()
        val D1: Double = stringD[1].toDouble()
        val FloatD = D0 / D1

        val stringM = DMS[1].split("/")
        val M0: Double = stringM[0].toDouble()
        val M1: Double = stringM[1].toDouble()
        val FloatM = M0 / M1

        val stringS = DMS[2].split("/")
        val S0: Double = stringS[0].toDouble()
        val S1: Double = stringS[1].toDouble()
        val FloatS = S0 / S1

        result = (FloatD + FloatM / 60 + FloatS / 3600) //.toFloat()
        return result
    }
}