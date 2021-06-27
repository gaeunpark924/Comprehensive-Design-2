package com.example.flatload

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        textView9.setOnClickListener {
            val i = Intent(this,InputWayActivity::class.java)
            startActivity(i)
        }
//        testBtn.setOnClickListener {
//            val i = Intent(this,MapActivity::class.java)
//            startActivity(i)
//        }
//        testGetBtn.setOnClickListener{
//            val i = Intent(this,TestGetServerActivity::class.java)
//            startActivity(i)
//        }
    }

}