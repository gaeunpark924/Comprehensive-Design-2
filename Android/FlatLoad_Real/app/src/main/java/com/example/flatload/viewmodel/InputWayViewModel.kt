package com.example.flatload.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.flatload.base.BaseViewModel
import com.example.flatload.model.DataModel


//InputWayActivity에서 로직 이동시켜야 함

class InputWayViewModel (private val model: DataModel): BaseViewModel() {
    private val _startLiveData = MutableLiveData<String>()
    private val _endLiveData = MutableLiveData<String>()
    val startLiveData: LiveData<String>             //view에서 observe하는 대상
        get() = _startLiveData
    val endLiveData: LiveData<String>
        get() = _endLiveData

    fun changeStart(type:String){
        _startLiveData.postValue(type)             //LiveData의 값을 변경
    }
    fun changeEnd(location:String){
        _endLiveData.postValue(location)
    }
}