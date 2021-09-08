package com.example.flatload

import android.app.Application
import com.example.flatload.di.myDiModule
import org.koin.android.ext.android.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(applicationContext, myDiModule) //koin
    }
}