package com.example.flatload.di

import com.example.flatload.FlatAPI
import com.example.flatload.model.DataModel
import com.example.flatload.model.DataModelImpl
import com.example.flatload.viewmodel.CameraViewModel
import com.example.flatload.viewmodel.InputWayViewModel
import com.example.flatload.viewmodel.MainViewModel
import com.google.gson.GsonBuilder
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory   //어뎁터 생성

//module 생성
var modelPart = module {
    //factory : DataModel 클래스를 상속받은 DataModelImpl라는 클래스를 만든다
    factory<DataModel> {
        DataModelImpl(get())
    }
}
var viewModelPart = module {
    //viewModel을 만든다
    //액티비티에서 by viewModel()을 통해서 얻어온다
    viewModel {
        MainViewModel(get())      //get() 모델을 얻어옴
    }
}
var viewModelCameraPart = module{
    viewModel{
        CameraViewModel(get())
    }
}

var viewModelInputWayPart = module{
    viewModel{
        InputWayViewModel(get())
    }
}

val gson = GsonBuilder().setLenient().create()
var retrofit = module{
    single<FlatAPI> {
        //로컬 서버와 같은 컴퓨터에서 애뮬레이터로 전송할 때 쓰는 주소 http://10.0.2.2:8080
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))   //응답 받은 json을 바로 data class로 바꿀 수 잇음
            .build()
            .create(FlatAPI::class.java)
    }
}
var myDiModule = listOf(modelPart, viewModelPart, viewModelCameraPart,viewModelInputWayPart ,retrofit)
