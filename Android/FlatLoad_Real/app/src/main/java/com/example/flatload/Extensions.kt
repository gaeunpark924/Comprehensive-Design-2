package com.example.flatload

import android.view.View

fun View.setOnSingleClickListener(onSingleClick: (View) -> Unit){
    val oneClick = OnSingleClickListener{
        onSingleClick(it)
    }
    setOnClickListener(oneClick)
}