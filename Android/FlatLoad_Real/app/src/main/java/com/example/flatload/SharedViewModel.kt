package com.example.flatload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject

open class SharedViewModel : ViewModel() {
//    val routeLiveData = MutableLiveData<Array<JsonObject>>()
////    val routeLiveData: LiveData<String>   //view에서 observe하는 대상
////        get() = _routeLiveData
////    fun changeRoute(routeStr : String){
////        _routeLiveData.postValue(routeStr)             //LiveData의 값을 변경
////        Log.d("ViewModel 안","")
////    }
//    val roadInfoLiveData = MutableLiveData<ArrayList<RoadviewInfo>>()
//    val dbInfoLiveData = MutableLiveData<ArrayList<DatabaseInfo>>()
//    fun changeRoute(r: Array<JsonObject>) {
//        routeLiveData.value = r
//    }
//    fun changeRoadInfo(v: ArrayList<RoadviewInfo>) {
//        roadInfoLiveData.value = v
//    }
//    fun changeDbInfo(d: ArrayList<DatabaseInfo>) {
//        dbInfoLiveData.value = d
//    }

    val infoLiveData = MutableLiveData<Array<Array<JsonObject>>>()
    fun changeInfo(i: Array<Array<JsonObject>>) {
        infoLiveData.value = i
    }

}