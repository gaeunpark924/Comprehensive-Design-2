package com.example.flatload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class SharedViewModel : ViewModel() {
    val routeLiveData = MutableLiveData<String>()
//    val routeLiveData: LiveData<String>   //view에서 observe하는 대상
//        get() = _routeLiveData
//    fun changeRoute(routeStr : String){
//        _routeLiveData.postValue(routeStr)             //LiveData의 값을 변경
//        Log.d("ViewModel 안","")
//    }
    fun changeRoute(routeStr: String) {
        routeLiveData.value = routeStr
    }
}