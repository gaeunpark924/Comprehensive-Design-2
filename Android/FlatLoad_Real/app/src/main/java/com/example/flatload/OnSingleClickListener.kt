package com.example.flatload

import android.os.SystemClock
import android.provider.Settings
import android.view.View

//중복 클릭 방지
class OnSingleClickListener(private var interval: Int = 60000,private var onSingleClick:(View) -> Unit) : View.OnClickListener {
    private var lastClickTime: Long = 0
    override fun onClick(p0: View?) {
        val elapsedRealtime = SystemClock.elapsedRealtime()  //앱 시작 시점부터 현재 까지 시간(ms)
        if ((elapsedRealtime - lastClickTime) < interval){
            return
        }
        lastClickTime = elapsedRealtime
        if (p0 != null) {
            onSingleClick(p0)
        }
    }
}