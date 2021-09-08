package com.example.flatload.view

import android.content.Intent

import com.example.flatload.viewmodel.MainViewModel
import com.example.flatload.R
import com.example.flatload.base.BaseActivity
import com.example.flatload.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {   //BaseActivity 상속
    override val layoutResourceId: Int
        get() = R.layout.activity_main
    override val viewModel: MainViewModel by viewModel("viewModelPart")//= MainViewModel()

    override fun initStartView() {
        //Log.d("MYTEST","출력")
//        lateinit var model : DataModel
//        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
//            .get(MainViewModel::class.java)
//        viewModel = ViewModelProvider(this, MainViewModel.Factory(model))
//            .get(MainViewModel::class.java)
        //init()
    }

    override fun initDataBinding() {
    }

    override fun initAfterBinding() {
        init()
    }
    private fun init() {
        textView9.setOnClickListener {
            val i = Intent(this, InputWayActivity::class.java)
            startActivity(i)
        }
        textView13.setOnClickListener {
            val i = Intent(this, CameraActivity::class.java)
            startActivity(i)
        }
    }
}